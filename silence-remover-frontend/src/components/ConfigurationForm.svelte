<script lang="ts">
	import { createEventDispatcher } from 'svelte';
	import ConfigurationInput from './ConfigurationInput.svelte';

	export let disabled = false;
	export let submitDisabled = false;

	let config = {
		minSegmentLength: 0.3,
		maxNegativeVolumeDeviation: 35,
		audibleSegmentPadding: 0.25,
	};

	let configValid = {
		minSegmentLength: true,
		maxNegativeVolumeDeviation: true,
		audibleSegmentPadding: true,
	};

	const dispatch = createEventDispatcher();

	const submit = () => {
		config = castValues(config);
		dispatch('configSubmitted', config);
	};

	const castValues = (conf) => {
		return {
			minSegmentLength: parseFloat(conf.minSegmentLength),
			maxNegativeVolumeDeviation: parseInt(conf.maxNegativeVolumeDeviation),
			audibleSegmentPadding: parseFloat(conf.audibleSegmentPadding),
		}
	};
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
		margin-top: 3rem;
		margin-bottom: 3rem;
	}

	button {
		position: relative;
		color: white;
		opacity: 1;
		background: transparent;
		overflow: hidden;
		z-index: 0;
	}
	button:after {
		content: '';
		position: absolute;
		left: 50%;
		top: 50%;
		transform: translate(-50%, -50%);
		z-index: -1;
		background-image: linear-gradient(70deg, rgba(0,194,203,1) 0%, rgba(39,61,205,1) 72%, rgba(95,39,205,1) 100%);
		animation-name: MOVE-BG;
		animation-duration: 3s;
		animation-timing-function: ease-in-out;
		animation-iteration-count: infinite;
		transform-origin: center center;
		width: 200%;
		height: 1000%;
	}
	@keyframes MOVE-BG {
		from {
			transform: translate(-50%, -50%) rotate(0);
		}
		to {
			transform: translate(-50%, -50%) rotate(360deg);
		}
	}

	button[disabled] {
		display: none;
	}
</style>

<form on:submit|preventDefault={submit}>
	<div class="params">
		<ConfigurationInput label="Minimum Segment Length"
							validate={v => v > 0}
							id="min-segment-length"
							type="number"
							step=0.01
							bind:value={config.minSegmentLength}
							on:valid={v => configValid.minSegmentLength = v.detail}
							hint="Minimum length a segment can be."
							unit="s"
							{disabled} />
		<ConfigurationInput label="Maximum Negative Volume Deviation"
							id="max-negative-volume-deviation"
							type="number"
							bind:value={config.maxNegativeVolumeDeviation}
							step=1
							on:valid={v => configValid.maxNegativeVolumeDeviation = v.detail}
							hint="Maximum negative volume deviation from the file's peak volume a segment is allowed to have in order to still be considered audible."
							unit="dB"
							{disabled} />
		<ConfigurationInput label="Audible Segment Padding"
							id="audible-segment-padding"
							type="number"
							validate={v => v >= 0}
							step=0.01
							bind:value={config.audibleSegmentPadding}
							on:valid={v => configValid.audibleSegmentPadding = v.detail}
							hint="Padding in seconds that's added around audible segments, so the audio doesn't feel as cut off."
							unit="s"
							{disabled} />
	</div>

	<div class="glow">
		<button type="submit" disabled={disabled || submitDisabled || !Object.values(configValid).every(v => v === true)}>
			REMOVE THE SILENCE!
		</button>
	</div>
</form>
