<!-- src/routes/register/+page.svelte -->

<script lang="ts">
    import { currentUser } from '$stores/user';
    import { goto } from '$app/navigation';

    let username = '';
    let email = '';
    let password = '';
    let confirmPassword = '';
    let message = '';
    let user;

    $: user = $currentUser;

    async function registerUser() {
        const params = new URLSearchParams();
        params.append('username', username);
        params.append('email', email);
        params.append('password', password);
        params.append('confirmPassword', confirmPassword);

        const res = await fetch('/sploinkyboinkend/users', {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            // Fetch the user details to get the userID
            const userRes = await fetch(`/sploinkyboinkend/users/${username}`);
            const userData = await userRes.json();
            currentUser.set(userData);
            message = 'User registered and logged in';
            await goto('/');
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Register</h1>

    {#if !user}
        <form on:submit|preventDefault={registerUser} class="max-w-md mx-auto">
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Username:</label>
                <input type="text" bind:value={username} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Email:</label>
                <input type="email" bind:value={email} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Password:</label>
                <input type="password" bind:value={password} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Confirm Password:</label>
                <input type="password" bind:value={confirmPassword} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <button type="submit" class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Register</button>
        </form>
    {:else}
        <p class="text-slate mb-4">You are already logged in as <strong>{user.username}</strong>.</p>
        <button on:click={() => currentUser.reset()} class="py-2 px-4 bg-red-500 hover:bg-red-600 text-white font-semibold rounded">Logout</button>
    {/if}

    {#if message}
        <p class="mt-4 text-slate">{message}</p>
    {/if}
</div>
