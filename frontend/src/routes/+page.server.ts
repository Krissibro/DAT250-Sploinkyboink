import type { PageServerLoad } from './$types';
import type { Poll } from '$lib/types';

export const load: PageServerLoad = async ({ fetch }) => {
    const res = await fetch('/sploinkyboinkend/polls/active');
    const data = await res.json();
    const polls: Poll[] = data.content; // Adjusting for paginated response
    return { polls };
};