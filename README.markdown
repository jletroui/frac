frac - fractal lines drawer
===========================

frac is an application allowing you to program an infinite number of fractal curves using simple [L-System](http://en.wikipedia.org/wiki/L-system) definitions.
L-System grammars allows to define complex looking curve with extremely simple definitions. It is in particular used with a good deal of success at modeling [plants](http://algorithmicbotany.org/papers/).
For a good introduction, I suggest reading this [paper](http://algorithmicbotany.org/papers/abop/abop-ch1.pdf) on the subject.


![Frac 1.0 screenshot](https://raw.github.com/jletroui/frac/master/frac_screenshot.png)

The same tree, with colors:

![Frac 1.0.1 screenshot](https://raw.github.com/jletroui/frac/master/frac_screenshot2.png)

frac programming is simple. You can type your definition as free text in the editor area, and lick "Refresh" to see the result.
Graphical primitives allows you to direct a virtual "turtle" that is holding a pen and reading a sequence of characters, as in the [LOGO programming language](http://en.wikipedia.org/wiki/Turtle_graphics).

Download and installation
=========================

You can download frac in the "Release" section of the Github repository. 

You need a Java runtime 8 or greater in order to run frac. On most systems, just double click on the jar you downloaded.
You can also type this command in a terminal:

    java -jar frac-1.0.5.jar

Most basic definition
=====================

A minimal definition has a *seed*. The seed of a definition is the starting point of the definition. It is sometimes called the 'axiom' or the 'initiator' in formal L-Systems.
You can visualize what the seed does by specifying a depth of 0, in which case no recursion will happen, and only the seed will be drawn.
Here is an example that will draw a square:

    seed = F+F+F+F

The seed is a constant. You can assign a value to a constant by using the '=' sign, and put the value on the right. See below for the list of available constants.
The seed will be the first *sequence* to be computed by frac. Each iteration, a new sequence will be computed using *replacement rules*.
You can control the number of iterations you want frac to compute by entering this number in the *depth* text field.

Graphical primitives
====================

The above definition is using 2 primitives: *F* and *+*. *F* instruct the turtle to move forward and to draw a line segment along the way. *+* is instructing the turtle to change its heading by turning on the left. Here are the list of all the drawing primitives supported by frac:

- **F**: move the turtle forward and draw the corresponding line segment. The following example is drawing a line:

        seed = F

- **f**: move the turtle forward, but do not draw anything. The following example is drawing a dashed line:

        seed = FfFfF

- **+**: turn the turtle left by a constant angle. You can modify this angle with the *angle* constant.
- **-**: turn the turtle right by a constant angle. You can modify this angle with the *angle* constant.
- **>**: shrinks the length of the turtle moves from now on. You can modify the shrinkage ratio with the *ratio* constant. The following example is drawing a spiral:

        seed = F>+F>+F>+F

- **<**: expands the length of the turtle moves from now on. This is the opposite of *<*.
- **[**: saves the turtle's position, heading, color, and moving length on top of a stack.
- **]**: unstack and restore the corresponding turtle's position, heading, and moving length. The following example is drawing 3 branches:

        seed = [F][+F][-F]
- **a positive integer**: the number of times the next graphical primitive will be applied. It only applies to the very next primitive, and only to 'F', 'f', '+' or '-'. Repetition count will be ignored for other primitive / rules. The following example is drawing a segment 11 times longer than usual:

        seed = 11F
- **{r,g,b} or {nameOfColor} or {changeInRed,changeInGreen,changeInBlue}**: sets the new stroke color by specifying either:
    - red, green, and blue decimal values: specifies a color by its RGB components.
    - color name: specifies a color by its name. Available colors are black, blue, cyan, darkGray, gray, green, lightGray, magenta, orange, pink, red, white, yellow.
    - increments (or decrements) of RGB components: this is changing the current active color.
    - The following example is drawing a black segment (default color) then a green segment, then a red:

        seed = F{green}F{+255,-255,+0}F

Definining replacement rules
============================

Replacement rules will instruct how to modify a sequence by replacing a given character in the sequence by an other character list. For example:

    seed = F
    F = F+F-F-F+F

The line below the seed is a rule. This rule instructs frac to replace each 'F' by 'F+F-F-F+F' at each iteration. Here is how the sequence will evolve for the first 3 iterations:

- **Depth 0**: F
- **Depth 1**: F+F-F-F+F
- **Depth 2**:  F+F-F-F+F+F+F-F-F+F-F+F-F-F+F-F+F-F-F+F+F+F-F-F+F

Note: you are not limited to grahpical primitives for your rules. Any character can be a rule. Characters that are not graphical primitives are simply ignored by the turtle. For example, here is the definition of the Dragon curve:

    seed = FX
    X = X+YF
    Y = FX-Y

You can see we introduced 2 new rules *X* and *Y* that are not graphical primitives. When the turtle will interpret those characters in the final sequence, it will simply do nothing.

Constants
=========

- **seed** - Mandatory: the initial sequence.
- **angle** - Optional, default = 90: the angle in whole degrees the turtle should turn when encountering a '+' or a '-'. Example with the Koch line:

        angle = 60
        seed = F
        F = F+F--F+F

- **ratio** - Optional, default = 0.5: the factor the turtle move length is affected when encountering a '>' or a '<'. Example with a spiral:

        ratio = 0.8
        seed = G
        G = G>F-

- **start** - Optional, default = left: where the turtle should be at the begining of the sequence. It can be either at the left of the screen, heading to the right, or at the bottom of the screen, heading to the top.
'left' is fine for most figure, but 'bottom' is usually preferred for tree-like strctures. Example with a tree:

        start = bottom
        angle = 25
        seed = F
        F = F[+F]F[-F]F

Samples
=======

All samples are availabe in the menu. You can see the entire list [here](https://github.com/jletroui/frac/blob/master/src/main/resources/definitions.frac).

Drawing statistics
==================

frac is giving you statistics on the curves currently displayed:

- **Turtle moves**: the number of time the turtle moved, wheter drawing or not. It is effectively a count of the 'F' and 'f' characters in the final sequence.
- **Turtle turns**: the number of time the turtle turned (ie: number of '-' and '+' in the final sequence).
- **Sequence length**: the total number of characters in the final sequence.
- **Drawing time**: in milliseconds. Look for this one when drawing deeper and deeper. It might take a very long time for frac to draw certain curves at a depth of more than 4.

Support
=======

You can submit bug reports [here on Github](https://github.com/jletroui/frac/issues).