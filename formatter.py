import ovito
from ovito.data import *
from PyQt5.QtCore import *
from PyQt5.QtGui import *

a1=1.51
a2=1.51
b1=19
b2=90

room_border_color = QColor(100,100,100)
width = 400.0
height = 200.0
y1 = height/2 - 25
y2 = height/2 + 25
border_lines = [
	[0.0, 0.0, width, 0.0], #Bottom
	[width, 0.0, width, height], #Right
	[0.0, 0.0, 0.0, height], #Left
	[0.0, height, width, height], #Top
	[width/2, height, width/2, y2], #Middle up
	[width/2, 0, width/2, y1] #Middle down
]
	

# This user-defined function is called by OVITO to let it draw arbitrary graphics on top of the viewport.
# It is passed a QPainter (see http://qt-project.org/doc/qt-5/qpainter.html).
def render(painter, **args):
	painter.setPen(QPen(room_border_color))
	for line in border_lines:
		painter.drawLine(a1*line[0]+b1,a2*line[1]+b2,a1*line[2]+b1,a2*line[3]+b2);