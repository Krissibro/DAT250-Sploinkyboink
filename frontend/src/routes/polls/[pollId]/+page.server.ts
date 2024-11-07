import type { PageServerLoad } from './$types';
import type { Poll } from '$lib/types';

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { pollID } = params;
    const res = await fetch(`/sploinkyboinkend/polls`);
    const data = await res.json();
    const polls: Poll[] = data.content; // Adjusting for paginated response
    const poll = polls.find((p) => p.pollID === pollID);

    if (poll) {
        return { poll };
    } else {
        return {
            status: 404,
            error: new Error('Poll not found'),
        };
    }
};
