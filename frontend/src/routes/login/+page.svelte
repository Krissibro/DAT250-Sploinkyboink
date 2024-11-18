<!-- src/routes/login/+page.svelte -->

<script lang="ts">
    import { currentUser } from '../../stores/user';
    import { goto } from '$app/navigation';

    let login = '';
    let password = '';
    let message = '';
    let user;


    $: user = $currentUser;

    async function loginUser() {
        const params = new URLSearchParams();
        params.append('username', login);
        params.append('password', password);


        const res = await fetch('/sploinkyboinkend/login', {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            // Fetch the user details
            const userRes = await fetch(`/sploinkyboinkend/users/${login}`);
            const userData = await userRes.json();
            currentUser.set(userData);
            message = 'Logged in successfully';
            await goto('/');
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Login</h1>

    {#if !user}
        <form on:submit|preventDefault={loginUser} class="max-w-md mx-auto">
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Username or Email:</label>
                <input type="text" bind:value={login} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Password:</label>
                <input type="password" bind:value={password} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <button type="submit" class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Login</button>
        </form>
    {:else}
        <p class="text-slate mb-4">You are already logged in as <strong>{user.username}</strong>.</p>
        <button on:click={() => currentUser.reset()} class="py-2 px-4 bg-red-500 hover:bg-red-600 text-white font-semibold rounded">Logout</button>
    {/if}

    {#if message}
        <p class="mt-4 text-slate">{message}</p>
    {/if}
</div>
