<script lang="ts">
    import '../app.css';
    import NavBar from '../components/NavBar.svelte';
    import { onMount } from 'svelte';
    import {currentUser} from "$stores/user";

    onMount(() => {
     fetch('/sploinkyboinkend/isLoggedIn')
         .then(response => {
             if(!response.ok){
                 currentUser.reset();
             }
         })
    });

</script>

<head>
    <title>Sploinkyboink</title>
</head>

<NavBar />
<slot />