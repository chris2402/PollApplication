# PollApplication  

[![Build Status](https://travis-ci.com/chris2402/PollApplication.svg?token=AsNzGpopnC9RcQp5F1w4&branch=dev)](https://travis-ci.com/chris2402/PollApplication)



#### Plugins   
Lombok plugin need to be install in order to run the application.


#### Design    
Initially in task A we wanted VoteDevice to be associated with a user, such that each user has an own 
voting device. An example of this use could be to have a collection of people wanting to 
               vote at the same time, because then they need a votingDevice each. After some discussion we figured out that it would be easier to implement the Device as just a Voter, 
not having to have a relationship with a user. An example of this use would be in a classroom where the students go and vote 
on the votingDevice after each other.    

Using this latter approach we could let a Device send in the number of yes and nos, count them and create
a new vote for each yes and no. The votes are then persisted using `voteRepository.saveAll(votes)`.

# Branch naming convention

## 1. Bruk prefixes
* feat/
* hotfix/
* fix/
* throwaway/  
etc..

## 2. Prøv å ha "short and sweet" navn

* ~~hotfix/bug-when-user-tries-to-use-æøå-in-poll-title-the-server-dies~~

* hotfix/æøå-crash


## 3. Ved merge skriv gjerne en liten "changelog" (spesielt på større merges)

Eksempel  

---

## Added
* Route guarding.  
  1. Private polls
  2. Admin page
  3. My polls page

## Changed
* Now always warns user before they can navigate back
* History mode instead of hash mode in router.

---

### Slett branch etter merge.


### Om merges også blir lagt labels på så er det litt enklere å lete etter konkrete merges senere ;) 

