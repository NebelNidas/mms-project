<script lang="ts">
	import { createEventDispatcher } from 'svelte';
	import ConfigurationInput from './ConfigurationInput.svelte';

	export let disabled = false;

	let config = {
		minSegmentLength: 3,
		maxVolume: 10,
		targetSpeed: 2,
		audioOnly: false
	};

	let configValid = {
		minSegmentLength: true,
		maxVolume: true,
		targetSpeed: true,
		audioOnly: true
	};

	const dispatch = createEventDispatcher();

	const submit = () => {
		dispatch('configSubmitted', config);
	}
</script>

<style>
	form {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.params {
		display: flex;
		justify-content: space-between;
		flex-wrap: wrap;
	}

	button {
		margin: 1rem;
		opacity: 1;
		transition: transform 0.3s ease-in-out, opacity 0.3s ease-in-out;
	}

	button[disabled] {
		opacity: 0;
		transform: scale(0%);
	}
</style>

<form on:submit|preventDefault={submit}>
	<div class="params">
		<ConfigurationInput label="Minimum Segment Length" validate={v => v > 0} id="min-segment-length" type="number" bind:value={config.minSegmentLength} on:valid={v => configValid.minSegmentLength = v.detail} {disabled} />
		<ConfigurationInput label="Maximum Volume" validate={v => v >= 0} id="max-volume" type="number" bind:value={config.maxVolume} on:valid={v => configValid.maxVolume = v.detail} {disabled} />
		<ConfigurationInput label="Target Speed" validate={v => v > 0} id="target-speed" type="number" bind:value={config.targetSpeed} on:valid={v => configValid.targetSpeed = v.detail} {disabled} />
		<ConfigurationInput label="Audio Only" id="audio-only" type="checkbox" bind:value={config.audioOnly} on:valid={v => configValid.audioOnly = v.detail} {disabled} />
	</div>

	<button type="submit" disabled={disabled || !Object.values(configValid).every(v => v === true)}>REMOVE THE SILENCE!</button>
</form>
