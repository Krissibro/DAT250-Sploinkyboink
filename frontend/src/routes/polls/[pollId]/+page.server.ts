import type { PageServerLoad } from './$types';
import type { Poll } from '$lib/types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollId } = params;
    const res = await fetch(`/sploinkyboinkend/polls/${pollId}`);
    const data = await res.json();
    const poll: Poll = data.content;

    if (poll) {
        return { poll };
    } else {
        return {
            status: 404,
            error: new Error('Poll not found'),
        };
    }
};
