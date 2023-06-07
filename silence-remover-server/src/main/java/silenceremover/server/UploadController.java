package silenceremover.server;

import job4j.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import silenceremover.SilenceRemover;
import silenceremover.config.ProjectConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

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
													@RequestParam("identifier") String identifier) throws IOException {
		if (identifier.length() < 20) {
			return ResponseEntity.badRequest().body("Invalid params!");
		}

		long timestamp = Instant.now().getEpochSecond();
		String id = identifier + timestamp;

		Path inPath = saveFile(file, id);
		Path outPath = Paths.get(FILE_STORAGE_PATH + File.separator + OUT_PATH + File.separator + identifier);

		ProjectConfig config = ProjectConfig.builder(inPath, outPath)
			// todo add silenceTimeThreshold
			.minSegmentLength(minSegmentLength)
			.maxVolume(maxVolume)
			.build();

		SseEmitter emitter = new SseEmitter();

		Job<File> job = new SilenceRemover(config).process();
		job.addProgressListener(progress -> {
			try {
				emitter.send(progress * 100);
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		});
		job.addCompletionListener((result, error) -> {
			if (result.isPresent()) {
				File f = result.get();
				try {
					emitter.send("COMPLETE");
					emitter.complete();
					progressService.remove(id);
					resultService.add(id, f.getAbsolutePath());
				} catch (IOException e) {
					emitter.completeWithError(e);
				}
			} else {
				error.ifPresent(emitter::completeWithError);
			}
		});

		progressService.add(id, emitter);
		job.run();
		return ResponseEntity.ok(String.valueOf(timestamp));
	}

	@GetMapping("/progress")
	public SseEmitter handleGetProgressSse(@RequestParam("identifier") String identifier) {
		if (!progressService.exists(identifier)) {
			throw new IllegalArgumentException();
		}
		return progressService.get(identifier);
	}

	private Path saveFile(MultipartFile file, String identifier) throws IOException {
		byte[] bytes = file.getBytes();
		Path path = Paths.get(FILE_STORAGE_PATH + File.separator + IN_PATH + File.separator + identifier);
		Files.write(path, bytes);
		return path;
	}

	@GetMapping("/result")
	public ResponseEntity<InputStreamResource> handleGetResult(@RequestParam("identifier") String identifier) throws FileNotFoundException {
		if (!resultService.exists(identifier)) {
			return ResponseEntity.badRequest().body(null);
		}
		File file = new File(resultService.get(identifier));
		InputStreamResource	resource = new InputStreamResource(new FileInputStream(file));

		return ResponseEntity.ok()
			.header("Content-Disposition", "attachment;filename=" + identifier)
			.contentType(MediaType.parseMediaType("application/octet-stream"))
			.contentLength(file.length())
			.body(resource);
	}
}
