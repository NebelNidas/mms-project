package silenceremover.jobs;

import job4j.JobCategory;

public final class JobCategories {
	public static final JobCategory PROCESS_FILE = new JobCategory("process-file");
	public static final JobCategory DETECT_INTERVALS = new JobCategory("detect-intervals", PROCESS_FILE);
	public static final JobCategory PROCESS_INTERVALS = new JobCategory("process-intervals", PROCESS_FILE);
	public static final JobCategory PROCESS_SEGMENTS = new JobCategory("process-segments", PROCESS_FILE);
	public static final JobCategory SPLIT_SEGMENTS = new JobCategory("split-segments", PROCESS_SEGMENTS);
	public static final JobCategory MERGE_SEGMENTS = new JobCategory("combine-segments", PROCESS_SEGMENTS);
}
