# planespace
simple 2D n-body orbit simulator
Simple simulator of gravitational interactions. This is a java learning project, no serious work.
Basic usage:
cursor keys      pan view
pg up/down       zoom out/in
+/-              time acceleration
1,2,3,4,5        switch to pre-programmed scenario (1 - partial solar system...5 just sun)
,.               prev/next spawned object
/                repeat last spawned object
move mouse+lmb   throws spawning object(s) into space. speed is determined by the mouse movement and is relative to time acceleration.
[]               select prev/next body
lmb              selects body nearest to mouse cursor
del              delete selected body(ies)
shift+mouse+lmb  select multiple object to group selection
space            clear group selection / cancel velocity adjustment
c                adjust velocity of selected bodies. Move mouse cursor to desired direction and pres middle-mouse-button to adjust the speed vector
l                lock view to selected body
v                togle speed and gravity acceleration vecotors of selected body
rmb              changes mouse into inspector which shows gravity acceleration vector at mouse location and speed of mouse move. drag while rmb to meassure distance.

How to have some fun:
Either run the executable jar or launch class "Program".
Upon starting, a partial solar system (button 1) is displayed. Use curor keys, pg up/dw +- [] l to get some basic idea about controlling the view.
Most entertining acivity is to press 5 to have only 1 sun in the space, then try to spawn another sun (.) and throw it to space in such a way
that the two suns orbit each other. If the attempt fails just press 5 and / to try again. It's also possible to display the vector of the selected object with v button
and try adjusting by enering control mode with c button and using mmb. Once the two suns orbit each other, try throwing among them an array of 49 moons.
Press . or , several times to find the array among spawning objects, then repeat the spawing of the last object by pressing /
Observing how the square array of moons get spaghettified when they pass around the sun or how they go around one sun and then gets atracted or consumed by the other
is somewhat satisfying ;) 

