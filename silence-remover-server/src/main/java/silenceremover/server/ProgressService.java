package silenceremover.server;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProgressService {
	private final ConcurrentHashMap<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

	public SseEmitter get(String id) {
		return sseEmitters.get(id);
	}

	public void add(String id, SseEmitter emitter) {
		sseEmitters.put(id, emitter);
	}

	public void remove(String id) {
		sseEmitters.remove(id);
	}

	public boolean exists(String id) {
		return sseEmitters.containsKey(id);
	}
}
