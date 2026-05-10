# Train Ticketing App

This is my solution for the train ticketing assessment (Problem 1). 

It is a small Java console app. You start it, you get a menu, you can book tickets, search trains between two stations, or open the admin menu.


## Implementation plan

These are the steps I followed:

1. I read the task and made a list of what the app must do (book, search, admin stuff, send emails, prevent overbooking, handle delays).
2. I decided to make it a console app and keep everything in memory. No database, since the task did not ask for one.
3. I made the domain classes first (Station, Route, Train, StopTime, Booking, Journey, JourneyLeg). They are just simple objects with fields and getters.
4. Then I made the repositories. Each one is an interface plus an in-memory class that stores stuff in a `HashMap`. I did it like this so I could change to a database later without touching the rest.
5. Then the services, where the real logic is:
   - `BookingService` checks the train exists, checks the route covers the segment, checks capacity, saves the booking, sends an email.
   - `ScheduleSearchService` finds trains between two stations. It handles direct trains and also one changeover.
   - `AdminService` does all the admin operations and also handles delays.
6. For emails I made an `EmailService` interface and a `ConsoleEmailService` that just prints the message. This way the project works without setting up a real email server, and in the tests I can use a fake one.
7. Then I made the CLI (`AppCli`) with the menus and `Scanner` for reading input. The CLI does not have any logic, it just calls the services.
8. I added some sample data (`SeedData`) so the app is not empty on start.
9. At the end I wrote unit tests with JUnit 5 for the main things.

## Design of the app

### Architecture

I used a layered architecture, this is what we learned in class:

```
cli         ->  AppCli, Main
service     ->  BookingService, ScheduleSearchService, AdminService, EmailService
repository  ->  StationRepository, RouteRepository, TrainRepository, BookingRepository
domain      ->  Station, Route, Train, StopTime, Booking, Journey, JourneyLeg
```

Each layer only uses the one below it. The CLI never touches the repositories directly, only the services. The services do not know there is a CLI. This makes it easier to change one part without breaking the others.

### Important classes

Domain (just data):
- `Station` - id and name.
- `Route` - id, name, and an ordered list of station ids. Has a `covers(from, to)` method I use a lot.
- `Train` - id, route, capacity, schedule, and a delay in minutes (0 by default). The delay shifts every arrival/departure time.
- `StopTime` - arrival and departure time at one station.
- `Booking` - id, customer email, train, from/to, seat count.
- `Journey` and `JourneyLeg` - what the search returns. One leg = one train.

Repositories - one interface and one in-memory class per entity. The interface only has the methods I actually need.

Services - `BookingService`, `ScheduleSearchService`, `AdminService`, plus the `EmailService` interface and its `ConsoleEmailService` impl.

CLI - `AppCli` for the menus and `Main` which creates everything and starts the app.

### Design patterns I used

I tried not to use too many patterns since this is a small project and the teacher said we should not add stuff just to look fancy. The ones I used:

- **Repository pattern** - so the services do not know how the data is stored. If I want a database later I just write a new repo class.
- **Strategy via interface** for `EmailService` - so the tests use a fake email service that records calls, and a real SMTP one could be added later.
- **Layered architecture** - already shown above.

Patterns I did not use even if I could have: factory, observer, singleton, builder. They did not bring anything useful here so I left them out.

### SOLID

- **S** (single responsibility) - every service does one thing. Booking service only books, search service only searches.
- **O** (open closed) - new email impl can be added by writing a new class, no need to change anything else.
- **L** (liskov) - any class that implements a repository interface can replace the in-memory one.
- **I** (interface segregation) - the repo interfaces only have the methods that are actually used.
- **D** (dependency inversion) - services depend on interfaces, not on concrete classes. Only `Main` knows the real classes.

### Streams and lambdas

I used streams where they were shorter than a loop, for example:
- summing the booked seats on a train (`BookingService`),
- filtering trains by route id and bookings by train id in the repos,
- the search code in `ScheduleSearchService`,
- sending the delay email to every distinct customer in `AdminService.markDelayed`.

### Things I simplified (so the reviewer knows)

- **Capacity check is simple.** A booking counts its seat count against the whole train run, even if the passenger only rides one segment. So if a train has cap 100 and someone books 100 seats from A to B, the train looks full for the rest of the trip. A more realistic check would look at each segment separately. I did not do that here to keep the code small.
- **Search supports up to one changeover.** Two or more changeovers would need a BFS over a graph. I did not have time for that.
- **Email is fake.** It just prints to the console. I made it an interface on purpose so a real SMTP impl could be plugged in later.

## Examples of input and output

The app starts with this data:

Stations: `BUC` Bucharest, `BRA` Brasov, `SIB` Sibiu, `CLU` Cluj, `TIM` Timisoara, and `IAS` Iasi (not connected, used to test the no-link case).

Routes:
- `R1`: BUC, BRA, SIB, CLU
- `R2`: BUC, TIM
- `R3`: SIB, TIM

Trains:
- `T1` (IR-101) on R1, cap 100. BUC 08:00, BRA 10:00, SIB 12:00, CLU 14:00.
- `T2` (IR-105) on R1, cap 50. BUC 14:00, BRA 16:00, SIB 18:00, CLU 20:00.
- `T3` (IR-200) on R2, cap 80. BUC 09:00, TIM 17:00.
- `T4` (IR-300) on R3, cap 60. SIB 13:00, TIM 15:00.

### 1) Book a ticket

Input (typed at the prompts):
```
1
student@example.com
1
T1
BUC
CLU
2
```

Output:
```
---- email (booking confirm) ----
to:      student@example.com
subject: booking 9c3ea83a confirmed
body:    your booking on train IR-101 from BUC to CLU for 2 seat(s) is confirmed.
---------------------------------
booked 1 ticket(s):
  Booking{9c3ea83a email=student@example.com train=T1 BUC->CLU seats=2}
```

### 2) Book several tickets at once

If you type `2` when it asks how many bookings, it will ask for the details two times and book both, sending one email per booking.

### 3) Overbooking is blocked

If a train is already full and you try to book again you get:
```
booking failed: train IR-101 is full: 100/100, req 1
```

### 4) Search a direct journey

Input:
```
2
BUC
CLU
```

Output:
```
found 2 journey(s):
Journey dep=08:00 arr=14:00 legs=1
  IR-101: BUC 08:00 -> CLU 14:00

Journey dep=14:00 arr=20:00 legs=1
  IR-105: BUC 14:00 -> CLU 20:00
```

### 5) Search a journey with changeover

Input:
```
2
BUC
TIM
```

Output:
```
found 2 journey(s):
Journey dep=08:00 arr=15:00 legs=2
  IR-101: BUC 08:00 -> SIB 12:00
  IR-300: SIB 13:00 -> TIM 15:00

Journey dep=09:00 arr=17:00 legs=1
  IR-200: BUC 09:00 -> TIM 17:00
```

The first one uses a changeover at Sibiu, the second one is the direct train.

### 6) Search with no link

Input:
```
2
BUC
IAS
```

Output:
```
error: no link between BUC and IAS
```

### 7) Admin - add a route

From the main menu pick `3` (admin) then `6` (add route). Then type id, name, and the station ids separated by commas. The new route shows up in the routes list.

### 8) Admin - add a train

Admin menu option `11`. It asks for the train id, name, route id, capacity, then the schedule one stop at a time.

### 9) Admin - modify a train

You can rename it (option 13) or change its capacity (option 14).

### 10) Admin - show bookings for a train

Admin menu option `15`, then the train id. Output:
```
bookings for T1: 2
  Booking{... email=student@example.com train=T1 BUC->CLU seats=2}
  Booking{... email=other@example.com   train=T1 BUC->BRA seats=1}
```

### 11) Admin - mark a train as delayed

Admin menu option `16`. Type the train id and the delay in minutes. Every customer that booked the train gets an email:
```
---- email (delay notice) ----
to:      student@example.com
subject: train IR-101 delayed
body:    your train IR-101 is delayed by 30 min.
------------------------------
```

If the same person booked the same train twice they only get one email (I used `distinct()` on the stream).

After the delay is set, the search results for that train automatically show the shifted times.

## Tests

I used JUnit 5. The tests are in `src/test/java`:
- `BookingServiceTest` - happy path, overbooking, train not found, wrong segment, book many.
- `ScheduleSearchServiceTest` - direct, changeover, no link, same station rejected.
- `AdminServiceTest` - add/remove route, rename train + change capacity, list bookings, delay sends one email per customer.

All 13 tests pass. Run them with `mvn test`.

---

## Problem 2

Problem 2 is a completely separate small Java app (a duplicate file finder). It is in the `problem2/` folder with its own README, pom and tests.
