export interface User {
    userID: number;
    username: string;
    email: string;
}

export interface Vote {
    id: number;
    user: User;
    voteOption: string;
    publishedAt: string; // ISO date string
    lastModifiedAt: string; // ISO date string
}

export interface Poll {
    pollID: string;
    byUser: User;
    question: string;
    publishedAt: string; // ISO date string
    lastModifiedAt: string; // ISO date string
    validUntil: string; // ISO date string
    voteOptions: string[];
    votes: Vote[];
}
