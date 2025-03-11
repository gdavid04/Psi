# Experimental feature branches

### [better-error-catcher](https://github.com/gdavid04/Psi/tree/better-error-catcher)
[![Download](https://img.shields.io/badge/download-better--error--catcher-9cf)](Psi 1.16 better-error-catcher.jar)  
Changes Error Catcher to output the error handled value instead of replacing the result of its target.  
Preview of what Error Catcher could become eventually.  
An improved version is part of the [Phi addon](https://github.com/gdavid04/phi)

### [error-catcher-and-compiler-rewrite](https://github.com/gdavid04/Psi/tree/error-catcher-and-compiler-rewrite)
Another implementation of the error catcher rework, also improving on compiler internals.  
Unfinished.

### [entity-eye-position](https://github.com/gdavid04/Psi/tree/entity-eye-position)
Changes Operator: Entity Position to always return foot position and adds Operator: Entity Eye Position.  
Might be merged eventually.  
Operator: Entity Eye and Foot Position are currently part of the [Phi addon](https://github.com/gdavid04/phi)

### [empty-cad-slot](https://github.com/gdavid04/Psi/tree/empty-cad-slot)
Adds an always empty slot to CADs, similar to tools and armor.  
Unlikely to be merged because "an odd number of slots looks cursed".

### [collapse-block-output](https://github.com/gdavid04/Psi/tree/collapse-block-output)
Changes Trick: Collapse Block to output the falling block entity created by the trick.

# Misc experiment branches
These have a zero chance of being merged and tend to be buggy.  
I made most of them to test super weird ideas that would never be implemented otherwise.

### [two-params-per-side](https://github.com/gdavid04/Psi/tree/two-params-per-side)
Allows two parameters to share a side.

### [big-grid](https://github.com/gdavid04/Psi/tree/big-grid)
Increases the spell grid size to 13x13  
The programmer GUI is buggy.

### [hexa-grid](https://github.com/gdavid04/Psi/tree/hexa-grid)
[![Download](https://img.shields.io/badge/download-hexa--grid-fc9)](Psi 1.16 hexa-grid.jar)  
Changes the spell grid to consist of hexagons instead of squares.  
The programmer GUI, connector lines and bandwidth calculation are very buggy.

### [cylinder](https://github.com/gdavid04/Psi/tree/cylinder)
Changes the shape of the spell grid from square to cylinder.  
The programmer GUI and bandwidth calculation are buggy.

### [trick-travel-time](https://github.com/gdavid04/Psi/tree/trick-travel-time)
Changes tricks to spawn motes that travel to the affected target from the focal point instead of affecting it directly and instantly.  
The target has a chance at evading the motes when moving fast enough. A very small instant hit range is preserved.  
Made as an experiment to rebalance the mod and add more visual effects.
