<script lang="ts">
    import { currentUser } from '../stores/user';

    function logout() {
        currentUser.reset();
    }
</script>

<nav class="bg-light-navy p-4">
    <div class="container mx-auto flex justify-between items-center">
        <ul class="flex space-x-4">
            <li><a href="/" class="text-lightest-slate hover:text-white">Home</a></li>
            <li><a href="/users" class="text-lightest-slate hover:text-white">Create User</a></li>
            <li><a href="/polls" class="text-lightest-slate hover:text-white">Create Poll</a></li>
        </ul>
        {#if $currentUser}
            <div class="text-lightest-slate">
                Logged in as: <span class="font-semibold">{$currentUser.username}</span>
                <button on:click={logout} class="ml-4 bg-red-500 hover:bg-red-600 text-white py-1 px-3 rounded">
                    Logout
                </button>
            </div>
        {/if}
    </div>
</nav>
