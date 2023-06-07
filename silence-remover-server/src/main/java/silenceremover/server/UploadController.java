package silenceremover.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import silenceremover.SilenceRemover;
import silenceremover.config.ProjectConfig;

@RestController
public class UploadController {
	@Value("${server.storage_path}")
	private String fileStoragePath;

	@PostMapping("/upload")
	public ResponseEntity<InputStreamResource> handlePostRequest(@RequestParam("file") MultipartFile file, @RequestParam("minSegmentLength") float minSegmentLength, @RequestParam("maxVolume") float maxVolume, @RequestParam("targetSpeed") float targetSpeed, @RequestParam("silenceTimeThreshold") float silenceTimeThreshold) {
		// todo where do we validate input?

		Path inPath, outPath;
		try {
			byte[] bytes = file.getBytes();
			String name = createFilename(bytes, "mp4");
			inPath = Paths.get(fileStoragePath + File.separator + "in" + File.separator + name);
			outPath = Paths.get(fileStoragePath + File.separator + "out" + File.separator + name);
			Files.write(inPath, bytes);
		} catch (IOException | NoSuchAlgorithmException e) {
			// todo handle error
			e.printStackTrace();

			return ResponseEntity.internalServerError().body(null);
		}


		ProjectConfig config = ProjectConfig.builder(inPath, outPath)
			.minSegmentMillis((int) minSegmentLength)
			.maxVolume((int) maxVolume)
			.targetSpeed(targetSpeed)
			.build();

		SilenceRemover sr = new SilenceRemover();
		sr.process(config, null);

		File processedFile = new File(outPath.toString());

		try {
			InputStreamResource resource = new InputStreamResource(new FileInputStream(processedFile));

			return ResponseEntity.ok()
				.header("Content-Disposition", "attachment;filename=" + processedFile.getName())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.contentLength(processedFile.length())
				.body(resource);
		} catch (IOException e) {
			// todo handle error
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		} finally {
			// todo remove the processed file after download
		}
	}

	private String createFilename(byte[] bytes, String extension) throws NoSuchAlgorithmException {
		return String.format("%s-%d.%s", getChecksum(bytes), Instant.now().getEpochSecond(), extension);
	}

	private String getChecksum(byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] checksum = md.digest(bytes);

		StringBuilder sb = new StringBuilder();
		for(byte b : checksum) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
