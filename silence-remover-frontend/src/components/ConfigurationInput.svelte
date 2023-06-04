<script lang="ts">
	import { createEventDispatcher } from 'svelte';

	export let label;
	export let type = 'text';
	export let value = '';
	export let id;
	export let required = false;
	export let disabled = false;
	export let validate = (v) => true;

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
	div {
		display: flex;
		flex-direction: column;
		margin-inline: 0.5rem;
	}

	.disabled {
		opacity: .5;
	}

	input:invalid {
		text-decoration: underline red;
		border: 1px solid red;
		background-color: #ff1c0f81;
	}
</style>

<div>
	<label for={id} class="{disabled ? 'disabled' : ''}">{label}</label>
	<input bind:this={inputElement} id={id} {type} {value} placeholder={label} on:input={handleChange} {required} {disabled} />
</div>
