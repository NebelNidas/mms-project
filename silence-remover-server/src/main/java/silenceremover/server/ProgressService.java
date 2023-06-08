package silenceremover.server;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import job4j.Job;

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
