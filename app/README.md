2020-03-14:
Just creating the project that Jonas gave me the idea for today. 
He wants an alarm app, but not quite as the ones that phones come with:

Alarms should not need to be shut off manually. Just sounding a sound/notification is enough.
Alarms should be able to repeat at given intervals, for X number of times. Used for yoga, when doing 
an exercise for a certain amount of time before switching.
Alarm sound should be selectable, and a setting for each alarm, not global.
Should be able to create as many alarms as desirable, and enable/disable them at will, with a switch.

A big question here is: Should the alarms only be active when within the app, or should one be able
to leave the app, and the alarms will still come on time? This makes quite a difference, as keeping
it strictly within the app is easier and will be more reliable, it's also flaky in that if you get a
call, or switch app or something, it might not work that well.
AFAIK, if you want to be able to leave the app, and have the alarm scheduled via the notification system,
I'm not sure if you can have an alarm that repeats, without some kind of manual intervention.

Keeping it all within the app, and not going via the Android scheduling and notification system, 
would probably be a lot easier. But this might require that we keep the screen active, so that the
app would not be put into the background, as that would cause the app to be in an inactive state,
which would render the alarms to not trigger. So.... questions...

Alarm types:

- Interval
    * Keeps screen on
    * Runs internal to app, not scheduled via system
    
(this is basically the clock app, so we could maybe skip this?)
- At given time of day 
    * Does not keep screen on
    * Schedules via system
    * Due to system scheduling, not as precise
    * Does not repeat or snooze (this is the diff from system)
    
More thoughts:

As far as I know, if you wanna schedule an alarm via the system, it must be of type notification. 
A notification can have a single sound, or repeating. But you'll have to dismiss the notification at 
some point.
If all you need is a regular sounding "ding" to tell you to shift positions, that would probably be 
a lot easier to hand-code and not use the Android notification system for. But the problem there is 
that when the screen goes off, the app is suspended, and so alarms will not go off, and hence, one 
would need permissions to keep the screen on, which again drains a lot of battery.

OR:
Solve it via a background service. This might be the best solution, as then the screen can turn off,
but the alarms should still sound. This would leave a permanent notification, so one would have to 
shut down the background service by dismissing the notification.
