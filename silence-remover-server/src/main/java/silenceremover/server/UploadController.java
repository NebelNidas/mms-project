package silenceremover.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
	@Value("${server.storage_path}")
	private String fileStoragePath;

	@PostMapping("/upload")
	public ResponseEntity<InputStreamResource> handlePostRequest(@RequestParam("file") MultipartFile file, @RequestParam("minSegmentLength") int minSegmentLength, @RequestParam("maxVolume") int maxVolume, @RequestParam("targetSpeed") int targetSpeed, @RequestParam("audioOnly") boolean audioOnly) {
		// todo validate input
		Path path = Paths.get(fileStoragePath + File.separator + file.getOriginalFilename());

		try {
			Files.createDirectories(path.getParent());
			Files.write(path, file.getBytes());
		} catch (IOException e) {
			// todo handle error
			e.printStackTrace();

			return ResponseEntity.internalServerError().body(null);
		}

		// todo process
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		String processedPathName = fileStoragePath + File.separator + file.getOriginalFilename();

		// todo really join the file path
		File processedFile = new File(processedPathName);

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
}
