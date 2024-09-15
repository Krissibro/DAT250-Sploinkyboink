<script lang="ts">
    import type { Poll } from '../../../types';
    import { currentUser } from '../../../stores/user';
    import { goto } from '$app/navigation';

    export let data: { poll: Poll };
    let selectedOption = '';
    let message = '';
    let user;

    $: user = $currentUser;

    function isLoggedIn() {
        return !!user;
    }

    async function vote() {
        if (!isLoggedIn()) {
            message = 'You must be logged in to vote';
            return;
        }

        const params = new URLSearchParams();
        params.append('username', user.username);
        params.append('voteOption', selectedOption);

        const res = await fetch(`/api/polls/${data.poll.pollId}/vote`, {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });

        if (res.ok) {
            await goto(`/polls/${data.poll.pollId}/votes`);
        } else {
            const errorText = await res.text();
            message = `Error: ${errorText}`;
        }
    }
</script>

<div class="container mx-auto p-4">
    <div class="bg-light-navy p-6 rounded shadow">
        <h1 class="text-3xl font-bold mb-4 text-lightest-slate">{data.poll.question}</h1>
        <p class="text-slate mb-2">Created by: {data.poll.byUser.username}</p>
        <p class="text-slate mb-2">Published at: {new Date(data.poll.publishedAt).toLocaleString()}</p>
        <p class="text-slate mb-4">Valid until: {new Date(data.poll.validUntil).toLocaleString()}</p>

        {#if isLoggedIn()}
            <form on:submit|preventDefault={vote}>
                <fieldset class="mb-4">
                    {#each data.poll.voteOptions as option}
                        <label class="block text-lightest-slate mb-2">
                            <input type="radio" name="voteOption" value={option} bind:group={selectedOption} class="mr-2" />
                            {option}
                        </label>
                    {/each}
                </fieldset>
                <button type="submit" class="py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded">Vote</button>
            </form>
        {:else}
            <p class="text-slate">You must be logged in to vote. <a href="/users" class="text-blue-400 hover:underline">Create User</a></p>
        {/if}

        {#if message}
            <p class="mt-4 text-red-500">{message}</p>
        {/if}
    </div>
</div>
