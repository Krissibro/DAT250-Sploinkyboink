<!-- src/routes/profile/+page.svelte -->

<script lang="ts">
    import { currentUser } from '$stores/user';
    import { goto } from '$app/navigation';

    let user: any;
    let newEmail = '';
    let newUsername = '';
    let newPassword = '';
    let confirmPassword = '';
    let message = '';

    $: user = $currentUser;

    function isLoggedIn() {
        return !!user;
    }

    async function updateEmail() {
        const params = new URLSearchParams();
        params.append('newEmail', newEmail);

        const res = await fetch(`/sploinkyboinkend/users/${user.userID}/email`, {
            method: 'PUT',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            user.email = newEmail;
            currentUser.set(user);
            message = 'Email updated successfully';
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }

    async function updateUsername() {
        const params = new URLSearchParams();
        params.append('newUsername', newUsername);

        const res = await fetch(`/sploinkyboinkend/users/${user.userID}/username`, {
            method: 'PUT',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            user.username = newUsername;
            currentUser.set(user);
            message = 'Username updated successfully';
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }

    async function updatePassword() {
        const params = new URLSearchParams();
        params.append('newPassword', newPassword);
        params.append('confirmPassword', confirmPassword);

        const res = await fetch(`/sploinkyboinkend/users/${user.userID}/password`, {
            method: 'PUT',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            message = 'Password updated successfully';
            newPassword = '';
            confirmPassword = '';
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    {#if isLoggedIn()}
        <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Profile</h1>
        <div class="max-w-md mx-auto">
            <div class="mb-6">
                <h2 class="text-xl font-semibold mb-2 text-lightest-slate">Update Email</h2>
                <input type="email" bind:value={newEmail} placeholder={user.email} class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white mb-2" />
                <button on:click={updateEmail} class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Update Email</button>
            </div>
            <div class="mb-6">
                <h2 class="text-xl font-semibold mb-2 text-lightest-slate">Update Username</h2>
                <input type="text" bind:value={newUsername} placeholder={user.username} class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white mb-2" />
                <button on:click={updateUsername} class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Update Username</button>
            </div>
            <div class="mb-6">
                <h2 class="text-xl font-semibold mb-2 text-lightest-slate">Change Password</h2>
                <input type="password" bind:value={newPassword} placeholder="New Password" class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white mb-2" />
                <input type="password" bind:value={confirmPassword} placeholder="Confirm Password" class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white mb-2" />
                <button on:click={updatePassword} class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Change Password</button>
            </div>
            {#if message}
                <p class="mt-4 text-slate">{message}</p>
            {/if}
        </div>
    {:else}
        <p class="text-slate mb-4">You must be logged in to view this page.</p>
        <a href="/login" class="py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Login</a>
    {/if}
</div>
