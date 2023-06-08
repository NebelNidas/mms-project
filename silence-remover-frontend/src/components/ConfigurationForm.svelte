<script lang="ts">
	import { createEventDispatcher } from 'svelte';
	import ConfigurationInput from './ConfigurationInput.svelte';

	export let disabled = false;
	export let submitDisabled = false;

	let config = {
		minSegmentLength: 0.3,
		maxVolume: -35,
		silenceTimeThreshold: 0.5,
	};

	let configValid = {
		minSegmentLength: true,
		maxVolume: true,
		silenceTimeThreshold: true,
	};

	const dispatch = createEventDispatcher();

	const submit = () => {
		config = castValues(config);
		dispatch('configSubmitted', config);
	};

	const castValues = (conf) => {
		return {
			minSegmentLength: parseFloat(conf.minSegmentLength),
			maxVolume: parseFloat(conf.maxVolume),
			silenceTimeThreshold: parseFloat(conf.silenceTimeThreshold),
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
		<ConfigurationInput label="Maximum Volume"
							id="max-volume"
							type="number"
							bind:value={config.maxVolume}
							step=0.01
							on:valid={v => configValid.maxVolume = v.detail}
							hint="Threshold of when something is classified as silent."
							unit="dB"
							{disabled} />
		<ConfigurationInput label="Silence Time Threshold"
							id="silence-time-threshold"
							type="number"
							validate={v => v >= 0}
							step=0.01
							bind:value={config.silenceTimeThreshold}
							on:valid={v => configValid.silenceTimeThreshold = v.detail}
							hint="Threshold  of the timespan when something should be cut out."
							unit="s"
							{disabled} />
	</div>

	<div class="glow">
		<button type="submit" disabled={disabled || submitDisabled || !Object.values(configValid).every(v => v === true)}>
			REMOVE THE SILENCE!
		</button>
	</div>
</form>
