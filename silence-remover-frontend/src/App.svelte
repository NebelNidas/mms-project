<script lang="ts">
	import FilePicker from './components/FilePicker.svelte';
	import ConfigurationForm from './components/ConfigurationForm.svelte';
	import LoadingAnimation from './components/LoadingAnimation.svelte';
	import ResultView from './components/ResultView.svelte';
	import ErrorModal from "./components/ErrorModal.svelte";

	const API_URL = process.env.API_URL;

	let selectedFile;
	let selectedConfig;
	let isLoading = false;
	let downloadLink = null;
	let error = null;

	const handleConfigSubmitted = (e) => {
		selectedConfig = e.detail;
		upload();
	};

	const downloadBlob = (blob, name) => {
		const url = URL.createObjectURL(blob);
		const link = document.createElement('a');
		link.href = url;
		link.download = name;
		document.body.appendChild(link);
		link.click();
		return document.body.removeChild(link);
	};

	const upload = async () => {
		isLoading = true;
		const formData = new FormData();

		formData.append('file', selectedFile);
		formData.append('minSegmentLength', selectedConfig.minSegmentLength);
		formData.append('maxVolume', selectedConfig.maxVolume);
		formData.append('targetSpeed', selectedConfig.targetSpeed);
		formData.append('audioOnly', selectedConfig.audioOnly);

		try {
			const response = await fetch(`${API_URL}/upload`, {
				method: 'POST',
				body: formData
			});
			const blob = await response.blob();
			downloadLink = downloadBlob(blob, selectedFile.name);
		} catch (e) {
			error = "Request failed: " + e.message;
		} finally {
			isLoading = false;
		}
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

<main>
	<header>
		<h1>Silence Remover</h1>
	</header>

	{#if isLoading}
		<LoadingAnimation />
	{:else if downloadLink}
		<ResultView link={downloadLink} />
	{:else}
		<FilePicker on:fileSelected={(e) => selectedFile = e.detail} />
		<ConfigurationForm on:configSubmitted={handleConfigSubmitted} disabled={selectedFile === undefined} />
	{/if}

	{#if error}
		<ErrorModal message={error} />
	{/if}
</main>
