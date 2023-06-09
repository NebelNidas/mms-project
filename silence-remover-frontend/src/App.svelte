<script lang="ts">
	import FilePicker from './components/FilePicker.svelte';
	import ConfigurationForm from './components/ConfigurationForm.svelte';
	import LoadingAnimation from './components/LoadingAnimation.svelte';
	import ResultView from './components/ResultView.svelte';
	import ErrorModal from './components/ErrorModal.svelte';
	import Header from './components/Header.svelte';
	import Navigation from './components/Navigation.svelte';
	import {onMount} from 'svelte';

	const API_URL = process.env.API_URL;

	let selectedFile;
	let selectedConfig;
	let isLoading = false;
	let downloadLink = null;
	let error = null;
	let identifier = null;
	let progress = null;

	onMount(async () => {
		checkExistingIdentifier();
	});

	const checkExistingIdentifier = async () => {
		identifier = getIdentifier();
		if (identifier) {
			if (await eventSourceExists(identifier)) {
				const eventSource = getEventSource(identifier);
				if (eventSource && eventSource.readyState !== EventSource.CLOSED) {
					getProgress(identifier).then(prog => progress = prog);
					handleProgress(eventSource);
					isLoading = true;
				}
			} else if (await resultExists()) {
				await handleResultReady(false);
			}
		}
	};

	const eventSourceExists = async identifier => {
		try {
			const response = await fetch(`${API_URL}/progress/exists?identifier=${encodeURIComponent(identifier)}`);
			return response.ok ? await response.text() === 'true' : false;
		} catch (e) {
			return false;
		}
	}

	const getProgress = async identifier => {
		try {
			const response = await fetch(`${API_URL}/progress/update?identifier=${encodeURIComponent(identifier)}`);
			return response.ok ? parseInt(await response.text()) : -1;
		} catch (e) {
			return -1;
		}
	}

	const resultExists = async () => {
		try {
			const response = await fetch(`${API_URL}/result/exists?identifier=${encodeURIComponent(identifier)}`);
			return response.ok ? await response.text() === 'true' : false;
		} catch (e) {
			return false;
		}
	}

	const cancel = async identifier => {
		setIdentifier('');
		location.reload();
		// Serverside job canceling is currently not supported
		/*try {
			const response = await fetch(`${API_URL}/job?identifier=${encodeURIComponent(identifier)}`, {method: 'DELETE'});
			if (!response.ok) {
				throw new Error("status " + response.status);
			}
		} catch (e) {
			error = "Failed to delete the job: " + e.message;
		}*/
	}

	const handleResultReady = async (autoDownload=true) => {
		isLoading = false;
		const response = await fetch(`${API_URL}/result?identifier=${encodeURIComponent(identifier)}`);
		const blob = await response.blob();
		downloadLink = downloadBlob(blob, selectedFile?.name || 'SilenceRemoved.mp4', autoDownload);
	};

	const handleProgress = (eventSource) => {
		eventSource.onmessage = event => {
			if (event.data === 'COMPLETED') {
				eventSource.close();
				handleResultReady();
			} else {
				progress = parseInt(event.data);
				if (progress === 100) {
					eventSource.close();
					handleResultReady();
				}
			}
		};

		eventSource.onerror = e => {
			error = "Progress subscription error!";
			isLoading = false;
			eventSource.close();
		};
	}

	const handleConfigSubmitted = (e) => {
		selectedConfig = e.detail;
		upload();
	};

	const downloadBlob = (blob, name, click=true) => {
		const url = URL.createObjectURL(blob);
		const link = document.createElement('a');
		link.href = url;
		link.download = name;
		document.body.appendChild(link);
		if (click) {
			link.click();
		}
		return document.body.removeChild(link);
	};

	const getIdentifier = () =>  localStorage.getItem('identifier');

	const setIdentifier = (identifier) => localStorage.setItem('identifier', identifier);

	const getEventSource = (identifier) => {
		return new EventSource(`${API_URL}/progress?identifier=${encodeURIComponent(identifier)}`);
	}

	const upload = async () => {
		isLoading = true;

		const ident = Math.random().toString(36).slice(-10) + Math.random().toString(36).slice(-10);

		const formData = new FormData();
		formData.append('file', selectedFile);
		formData.append('minSegmentLength', selectedConfig.minSegmentLength);
		formData.append('maxVolume', selectedConfig.maxVolume);
		formData.append('silenceTimeThreshold', selectedConfig.silenceTimeThreshold);
		formData.append('identifier', ident);

		fetch(`${API_URL}/upload`, { method: 'POST', body: formData})
			.then(async response => {
				if (!response.ok) {
					throw new Error(`HTTP error! ${await response.text()}`);
				}
				return response.text();
			})
			.then(stamp => {
				identifier = ident + stamp;
				setIdentifier(identifier);

				const eventSource = getEventSource(identifier);
				handleProgress(eventSource);
			})
			.catch(e => {
				isLoading = false;
				error = e.message;
			});
	}

</script>

<style>
	main {
		max-width: 80%;
		margin: 4rem auto auto;
		display: flex;
		align-items: center;
		flex-direction: column;
	}
</style>

<Header />
<main>
	{#if isLoading}
		<LoadingAnimation bind:progress={progress} handleCancel={cancel} />
	{:else if downloadLink}
		<ResultView link={downloadLink} />
	{:else}
		<FilePicker on:fileSelected={(e) => selectedFile = e.detail} />
		<ConfigurationForm on:configSubmitted={handleConfigSubmitted} submitDisabled={selectedFile === undefined} />
	{/if}

	{#if error}
		<ErrorModal bind:message={error} on:closed={checkExistingIdentifier}/>
	{/if}
</main>

<Navigation />
