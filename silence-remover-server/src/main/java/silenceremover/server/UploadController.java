package silenceremover.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import job4j.Job;

import silenceremover.SilenceRemover;
import silenceremover.config.ProjectConfig;

@RestController
public class UploadController {
	private static final String OUT_PATH = "out";
	private static final String IN_PATH = "in";

	@Value("${server.storage_path}")
	private String FILE_STORAGE_PATH;

	private final ProgressService progressService;
	private final ResultService resultService;

	public UploadController(ProgressService progressService, ResultService resultService) {
		this.progressService = progressService;
		this.resultService = resultService;
	}

	@PostMapping("/upload")
	public ResponseEntity<String> handlePostRequest(@RequestParam("file") MultipartFile file,
													@RequestParam("minSegmentLength") float minSegmentLength,
													@RequestParam("maxVolume") float maxVolume,
													@RequestParam("silenceTimeThreshold") float silenceTimeThreshold,
													@RequestParam("identifier") String id) throws IOException {
		if (id.length() < 20) {
			return ResponseEntity.badRequest().body("Invalid params!");
		}

		long timestamp = Instant.now().getEpochSecond();
		String identifier = id + timestamp;

		Path inPath = saveFile(file, identifier);
		Path outPath = Paths.get(FILE_STORAGE_PATH + File.separator + OUT_PATH + File.separator + identifier + ".mp4");

		ProjectConfig config = ProjectConfig.builder(inPath, outPath)
				// todo add silenceTimeThreshold
				.minSegmentLength(minSegmentLength)
				.maxVolume(maxVolume)
				.build();

		Job<File> job = new SilenceRemover(config).process();
		addListeners(job, identifier);

		job.run();
		progressService.addJob(identifier, job.asFuture());
		return ResponseEntity.ok(String.valueOf(timestamp));
	}

	@DeleteMapping("/job")
	public ResponseEntity<String> handleDeleteUpload(@RequestParam("identifier") String identifier) {
		if (progressService.existsJob(identifier)) {
			progressService.getJob(identifier).cancel(true);
		}

		return ResponseEntity.ok().body("ok");
	}

	private SseEmitter addListeners(Job<File> job, String identifier) {
		SseEmitter emitter = new SseEmitter(1000L * 60 * 60 * 60);
		AtomicInteger lastSentProgress = new AtomicInteger(-1);
		job.addProgressListener(progress -> {
			try {
				int percentage = (int) (progress * 100);

				if (percentage > lastSentProgress.get()) {
					try {
						emitter.send(percentage);
						progressService.setLastProgress(identifier, percentage);
					} catch (IllegalStateException e) {
						// not beautiful, but currently the best workaround
						lastSentProgress.set(1000);
					}
				}
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		});
		job.addCompletionListener((result, error) -> {
			if (result.isPresent()) {
				File f = result.get();

				try {
					emitter.send("COMPLETED");
					emitter.complete();
					progressService.removeJob(identifier);
					resultService.add(identifier, f.getAbsolutePath());
				} catch (IllegalStateException e) {
					lastSentProgress.set(1000);
				} catch (IOException e) {
					emitter.completeWithError(e);
				}
			} else {
				error.ifPresent(emitter::completeWithError);
			}
		});
		return emitter;
	}

	@GetMapping("/progress/exists")
	public boolean handleGetProgressExists(@RequestParam("identifier") String identifier) {
		return progressService.existsJob(identifier) && !progressService.getJob(identifier).isDone() && !progressService.getJob(identifier).isCancelled();
	}

	@GetMapping("/progress")
	public SseEmitter handleGetProgressSse(@RequestParam("identifier") String identifier) {
		if (!progressService.existsJob(identifier)) {
			throw new IllegalArgumentException();
		}

		return addListeners(progressService.getJob(identifier).getUnderlyingJob(), identifier);
	}

	@GetMapping("/progress/update")
	public int handleGetProgressUpdate(@RequestParam("identifier") String identifier) {
		if (!progressService.existsLastProgress(identifier)) {
			return -1;
		}

		return progressService.getLastProgress(identifier);
	}

	private Path saveFile(MultipartFile file, String identifier) throws IOException {
		byte[] bytes = file.getBytes();
		Path path = Paths.get(FILE_STORAGE_PATH + File.separator + IN_PATH + File.separator + identifier + ".mp4");
		Files.write(path, bytes);
		return path;
	}

	@GetMapping("/result")
	public ResponseEntity<InputStreamResource> handleGetResult(@RequestParam("identifier") String identifier) throws FileNotFoundException {
		if (!resultService.exists(identifier)) {
			return ResponseEntity.badRequest().body(null);
		}

		File file = new File(resultService.get(identifier));
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return ResponseEntity.ok()
			.header("Content-Disposition", "attachment;filename=" + identifier)
			.contentType(MediaType.parseMediaType("application/octet-stream"))
			.contentLength(file.length())
			.body(resource);
	}

	@GetMapping("/result/exists")
	public boolean handleGetResultExists(@RequestParam("identifier") String identifier) {
		return resultService.exists(identifier);
	}
}
