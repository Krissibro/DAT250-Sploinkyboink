<script lang="ts">
    import { currentUser } from '$stores/user';
    import { goto } from '$app/navigation';

    let question = '';
    let optionsText = '';
    let message = '';
    let user : any;

    $: user = $currentUser;

    function isLoggedIn() {
        return !!user;
    }

    async function createPoll() {
        if (!isLoggedIn()) {
            message = 'You must be logged in to create a poll';
            return;
        }

        const voteOptions = optionsText.split('\n').map((s) => s.trim()).filter(Boolean);

        const params = new URLSearchParams();
        params.append('username', user.username);
        params.append('question', question);
        voteOptions.forEach((option) => params.append('voteOptions', option));

        const res = await fetch('/sploinkyboinkend/polls', {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            const responseText = await res.text();
            const pollIdMatch = responseText.match(/Poll created with ID: (.+)/);
            if (pollIdMatch) {
                const pollId = pollIdMatch[1];
                await goto(`/polls/${pollId}`);
            } else {
                message = responseText;
            }
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Create Poll</h1>

    {#if isLoggedIn()}
        <form on:submit|preventDefault={createPoll} class="max-w-md mx-auto">
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Question:</label>
                <input type="text" bind:value={question} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" />
            </div>
            <div class="mb-4">
                <label class="block text-lightest-slate mb-2">Options (one per line):</label>
                <textarea bind:value={optionsText} required class="w-full p-2 rounded bg-lightest-navy text-white border border-slate focus:outline-none focus:border-white" rows="5"></textarea>
            </div>
            <button type="submit" class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Create Poll</button>
        </form>
    {:else}
        <p class="text-slate mb-4">You must be logged in to create a poll. <a href="/users" class="text-blue-400 hover:underline">Create User</a></p>
    {/if}

    {#if message}
        <p class="mt-4 text-slate">{message}</p>
    {/if}
</div>
