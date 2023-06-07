package silenceremover.server;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResultService {
	private final ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();

	public String get(String id) {
		return results.get(id);
	}

	public void add(String id, String name) {
		results.put(id, name);
	}

	public void remove(String id) {
		results.remove(id);
	}

	public boolean exists(String id) {
		return results.containsKey(id);
	}
}
