package silenceremover.server;

import job4j.Job;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class ProgressService {
	private final ConcurrentHashMap<String, Job.JobFuture<File>> jobs = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Integer> lastProgress = new ConcurrentHashMap<>();

	public Job.JobFuture<File> getJob(String id) {
		return jobs.get(id);
	}

	public void addJob(String id, Job.JobFuture<File> job) {
		jobs.put(id, job);
	}

	public void removeJob(String id) {
		jobs.remove(id);
	}

	public boolean existsJob(String id) {
		return jobs.containsKey(id);
	}

	public void setLastProgress(String id, int progress) {
		lastProgress.put(id, progress);
	}

	public int getLastProgress(String id) {
		return lastProgress.get(id);
	}

	public boolean existsLastProgress(String id) {
		return lastProgress.containsKey(id);
	}
}
