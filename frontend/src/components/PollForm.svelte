<script lang="ts">
    export let question = '';
    export let optionsText = '';
    export let validUntil = '';
    export let onSubmit: () => void;
    export let buttonText = 'Submit';
</script>

<form on:submit|preventDefault={onSubmit} class="max-w-md mx-auto bg-light-navy p-6 rounded-lg shadow-md">
    <div class="mb-4">
        <label class="block text-lightest-slate mb-2 font-semibold">Question:</label>
        <input
                type="text"
                bind:value={question}
                required
                class="w-full p-2 rounded bg-navy text-white border border-slate-700 focus:outline-none focus:border-blue-500"
                placeholder="Enter your poll question here..."
        />
    </div>
    <div class="mb-4">
        <label class="block text-lightest-slate mb-2 font-semibold">Options (one per line):</label>
        <textarea
                bind:value={optionsText}
                required
                class="w-full p-2 rounded bg-navy text-white border border-slate-700 focus:outline-none focus:border-blue-500"
                rows="5"
                placeholder="Option 1&#10;Option 2&#10;Option 3"
        ></textarea>
    </div>
    <div class="mb-4">
        <label class="block text-lightest-slate mb-2 font-semibold">Valid Until:</label>
        <input
                type="datetime-local"
                bind:value={validUntil}
                required
                class="w-full p-2 rounded bg-navy text-white border border-slate-700 focus:outline-none focus:border-blue-500"
        />
    </div>
    <button
            type="submit"
            class="w-full py-2 px-4 bg-green-600 hover:bg-green-700 text-white font-semibold rounded"
    >
        {buttonText}
    </button>
</form>
