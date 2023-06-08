<script lang="ts">
	import { createEventDispatcher } from 'svelte';

	export let label;
	export let type = 'text';
	export let value = '';
	export let id;
	export let required = false;
	export let disabled = false;
	export let validate = (v) => true;
	export let step;
	export let hint = 'tmp hint todo';
	export let unit = '';

	let inputElement;

	const dispatch = createEventDispatcher();

	const handleChange = (e) => {
		value = value = type === 'checkbox' ? e.target.checked : e.target.value;
		if (validate(value)) {
			inputElement.setCustomValidity('');
			dispatch('change', value);
		} else {
			inputElement.setCustomValidity('Invalid');
		}
		dispatch('valid', inputElement.validity.valid);
	};
</script>

<style>
	.container {
		display: flex;
		flex-direction: column;
		margin-inline: 0.5rem;
	}

	.disabled {
		opacity: .5;
	}

	input:invalid {
		color: red;
		border: 1px solid red;
	}

	label {
		display: block;
		font-size: 14px;
		font-weight: 200;
		color: #333;
	}

	input {
		display: block;
		width: 100%;
		padding: 0.5em;
		font-size: 1em;
		border: 1px solid #ccc;
		border-radius: 8px;
		box-sizing: border-box;
		outline: none;
	}

	input[type="checkbox"] {
		flex: 1;
		margin: 0.4rem;
	}

	.input {
		position: relative;
		display: inline-block;
	}

	.input input {
		padding-right: 30px;
	}

	.input-unit {
		position: absolute;
		right: 5px;
		top: 50%;
		transform: translateY(-50%);
		background-color: transparent;
		color: #666;
	}

	.badge {
		position: absolute;
		top: -8px;
		right: -8px;

	}

	.icon {
		background-color: #fff;
		color: #000;
		padding: 2px 7px;
		border-radius: 50%;
		font-size: 0.7em;
		border: 1px solid #00c2cb;
	}

	.hint {
		display: none;
		position: absolute;
		color: #000;
		padding: 10px;
		border-radius: 5px;
		white-space: nowrap;
		box-shadow: rgba(0, 0, 0, 0.35) 0 5px 15px;
		background: white;
		z-index: 2;
		top: calc(-20px - 1ch);
		left: -550%;
		font-size: 12px;
	}

	.badge:hover .hint {
		display: block;
	}

	.badge:hover i {
		box-shadow: rgba(50, 50, 93, 0.25) 0px 30px 60px -12px inset, rgba(0, 0, 0, 0.3) 0px 18px 36px -18px inset;
	}
</style>

<div class="container">
	<label for={id} class="{disabled ? 'disabled' : ''}">{label}</label>
	<div class="input">
		<input bind:this={inputElement} id={id} {type} {step} {value} placeholder={label} on:input={handleChange} {required} {disabled} />
		<span class="input-unit">{unit}</span>
		<div class="badge">
			<i class="icon">i</i>
			<span class="hint">{hint}</span>
		</div>
	</div>
</div>
