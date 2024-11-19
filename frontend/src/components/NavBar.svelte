<script lang="ts">
    import { currentUser } from '../stores/user';
    import { goto } from '$app/navigation';

    function logout() {
       fetch('/sploinkyboinkend/logout', {
            method : 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
         });
        currentUser.reset();
        goto('/');
    }
</script>

<nav class="bg-light-navy p-4">
    <div class="container mx-auto flex justify-between items-center">
        <ul class="flex space-x-4">
            <li><a href="/" class="text-lightest-slate hover:text-white">Home</a></li>
            <li><a href="/polls/create" class="text-lightest-slate hover:text-white">Create Poll</a></li>
            {#if $currentUser}
                <li><a href="/profile" class="text-lightest-slate hover:text-white">Profile</a></li>
            {/if}
        </ul>
        {#if $currentUser}
            <div class="text-lightest-slate flex items-center">
                Logged in as: <span class="font-semibold ml-1">{$currentUser.username}</span>
                <button on:click={logout} class="ml-4 bg-red-500 hover:bg-red-600 text-white py-1 px-3 rounded">
                    Logout
                </button>
            </div>
        {:else}
            <div>
                <a href="/login" class="text-lightest-slate hover:text-white mr-4">Login</a>
                <a href="/register" class="text-lightest-slate hover:text-white">Register</a>
            </div>
        {/if}
    </div>
</nav>
