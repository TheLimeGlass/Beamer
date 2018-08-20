# Beamer
A Skript addon dedicated to making guardian beams.

Example:
```
command /beam:
	trigger:
		set {_beam} to a new beam between player and target block for player
		start beam {_beam}
		wait 5 seconds
		stop beam {_beam}
		broadcast "stopping"
```

Syntax:
```
Syntax:
  Conditions:
    CondBeamIsActive:
      enabled: true
      description: Check if the beam is active.
      syntax:
      - '%beam% (1¦is|2¦is(n''t|not)) active'
    CondBeamIsViewing:
      enabled: true
      description: Check if the player is currently viewing the beam.
      syntax:
      - '%beam% (1¦is|2¦is(n''t|not)) [currently] viewable'
  Effects:
    EffUpdateBeam:
      enabled: true
      description: Update Guardian Beam.
      syntax:
      - update [beam[s]] %beams%
    EffStartEndLocationBeam:
      enabled: true
      description: Set the starting or ending location of the beam.
      syntax:
      - set [the] (1¦starting|2¦ending) (location|postions)[s] of [(all [[of] the]|the)]
        [beam[s]] %beams% to %location%
    EffStartStopBeam:
      enabled: true
      description: Starts or stops a Guardian Beam.
      syntax:
      - (1¦start|2¦stop) [beam[s]] %beams%
  Expressions:
    ExprNewBeam:
      enabled: true
      description: Creates a new beam.
      syntax:
      - '[new] beam (between|from) %location% (to|and) %location% for %player%
        [[(with|in)] radius %-number% [and] update delay %-number%]'
```