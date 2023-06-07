<script>
	import { FontAwesomeIcon } from '@fortawesome/svelte-fontawesome';
	import { faGithub } from '@fortawesome/free-brands-svg-icons';
</script>

<style>
	a {
		text-decoration: none;
		color: #5f27cd;
		font-size: 2em;
	}

	nav {
		position: absolute;
		right: 0;
		top: 0;
		padding: 1rem;
	}
</style>

<nav>
	<a href="https://github.com/NebelNidas/silence-remover">
		<FontAwesomeIcon icon={faGithub} />
	</a>
</nav>
