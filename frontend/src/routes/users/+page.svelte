<script lang="ts">
    import { currentUser } from '../../stores/user';
    let username = '';
    let email = '';
    let message = '';
    let user;

    $: user = $currentUser;

    async function createUser() {
        const params = new URLSearchParams();
        params.append('username', username);
        params.append('email', email);

        const res = await fetch('/api/users', {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            const user = { username, email };
            currentUser.set(user);
            message = 'User created and logged in';
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Create User</h1>

    {#if !user}
        <form on:submit|preventDefault={createUser} class="max-w-md mx-auto">
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Username:</label>
                <input type="text" bind:value={username} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Email:</label>
                <input type="email" bind:value={email} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <button type="submit" class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Create User</button>
        </form>
    {:else}
        <p class="text-slate mb-4">You are already logged in as <strong>{user.username}</strong>.</p>
        <button on:click={() => currentUser.reset()} class="py-2 px-4 bg-red-500 hover:bg-red-600 text-white font-semibold rounded">Logout</button>
    {/if}

    {#if message}
        <p class="mt-4 text-slate">{message}</p>
    {/if}
</div>
