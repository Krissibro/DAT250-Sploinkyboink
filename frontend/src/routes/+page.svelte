<script lang="ts">
    import type { Poll } from '$lib/types';
    import PollCard from '../components/PollCard.svelte';
    import { goto } from '$app/navigation';

    export let data: { polls: Poll[]; pagination: any };

    $: polls = data.polls;
    $: pagination = data.pagination;

    // Reactive declaration for page numbers
    let pageNumbers: number[] = [];
    $: {
        pageNumbers = [];
        for (let i = pagination.page - 2; i <= pagination.page + 2; i++) {
            if (i >= 0 && i < pagination.totalPages) {
                pageNumbers.push(i); // Convert to 1-based page numbers
            }
        }
    }

    let isLoading = false;

    const navigateToPage = async (page: number) => {
        if (page === pagination.page) return;
        isLoading = true;
        try {
            await goto(`/?page=${page}`, { replaceState: false });
        } catch (error) {
            console.error('Navigation error:', error);
        } finally {
            isLoading = false;
        }
    };
</script>


<div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6 text-lightest-slate">Active Polls</h1>

    {#if polls.length > 0}
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {#each polls as poll}
                <PollCard {poll} />
            {/each}
        </div>

        <!-- Pagination Controls -->
        <div class="flex justify-center mt-6">
            <!-- Previous Button -->
            {#if !pagination.first}
                <button
                        on:click={() => navigateToPage(pagination.page - 1)}
                        class="px-4 py-2 mx-1 bg-light-navy text-lightest-slate rounded hover:bg-slate-700 disabled:bg-gray-600"
                        disabled={isLoading}>
                    Previous
                </button>
            {/if}

            <!-- Page Number Buttons -->
            {#each pageNumbers as pageNumber}
                <button
                    on:click={() => navigateToPage(pageNumber)}
                    class="px-4 py-2 mx-1 {pagination.page === pageNumber ? 'bg-slate-700' : 'bg-light-navy'} text-lightest-slate rounded hover:bg-slate-700 disabled:bg-gray-600"
                    disabled={pagination.page === pageNumber || isLoading}>
                    {pageNumber + 1}
                </button>
            {/each}

            <!-- Next Button -->
            {#if !pagination.last}
                <button
                        on:click={() => navigateToPage(pagination.page + 1)}
                        class="px-4 py-2 mx-1 bg-light-navy text-lightest-slate rounded hover:bg-slate-700 disabled:bg-gray-600"
                        disabled={isLoading}>
                    Next
                </button>
            {/if}
        </div>

        <p class="text-slate mt-4 text-center">
            Page {pagination.page + 1} of {pagination.totalPages}
        </p>
    {:else}
        <p class="text-slate text-center">No active polls available.</p>
    {/if}
</div>
