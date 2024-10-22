# Welcome to SploinkyBoink

![Cat](./images/cat.jpg)


## Poll System Business Logic (From ChatGPT)


The business logic for this application centers around three main entities: **Users**, **Polls**, and **Votes**. Each entity has defined actions within the system, facilitated by the `PollController` and `PollService` classes. The system also enforces important constraints to ensure business rules are followed.

## 1. User Management
- Users can be created, retrieved, and deleted within the system.  
**Constraints**: A user must exist before creating a poll.

## 2. Poll Management
- Users can manage polls, including creating, retrieving, and deleting polls.  
**Constraints**: A poll cannot be modified or deleted if it has expired or is already closed and guest users cannot create polls.  

## 3. Voting Logic
- Users can vote on a poll by choosing one of the available options.
- Users can modify their existing vote on a poll.
- Users can delete their votes.
- All votes cast in a specific poll can be retrieved. 
**Constraints**: Users cannot vote or delete or modify their votes on polls that have expired. 

## Sequence diagram 
The sequence diagram shows interactions for voting on a poll. It captures the flow of events when a user submits a vote, with communication between the controller, service, and domain models.

### Voting Process Sequence:
1. User requests to vote on a poll.
2. The `PollController` handles the request and forwards it to the `PollService`.
3. The `PollService` retrieves the `Poll` and validates if the poll exists, the user exists, and the vote option is valid.
4. The `PollService` calls the `Poll.vote()` method.
5. The vote is recorded if all conditions are met.

### Sequence Diagram (in text-based UML notation):
```rust
User -> PollController: submitVote(pollId, voteOption)
PollController -> PollService: voteOnPoll(pollId, username, voteOption)
PollService -> Poll: getPoll(pollId)
PollService -> User: getUser(username)
Poll -> PollService: return poll
User -> PollService: return user

PollService -> Poll: vote(user, vote)
Poll -> PollService: vote success
PollService -> PollController: return vote success
PollController -> User: vote success



