package orbit;

import java.awt.Component;
import java.awt.EventQueue;


public class Program {

	// private JPanelView jpView;
	// private CanvasView canvas;
	private Component painter;
	private SpaceLogic space;
	private PaintHandler paintHandler;
	private Input input;

	private static String painterMode = "JPanel";
	// private static int initStateArg;

	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");


		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				System.out.println(args[i]);
				if (args[i].equals("canvas")) {
					painterMode = "Canvas";
				}
			}
		}

		//System.out.println("Drawing on " + painterMode);
		TextOutput textOutput = new TextOutput();
		textOutput.addMessage("Drawing on " + painterMode);
		new Program();

	}

	public Program() {

		space = new SpaceLogic();
		input = new Input(space);
		paintHandler = new PaintHandler(space, input);
		space.addPaintHandler(paintHandler);
		space.addInput(input);
		input.addPaintHandler(paintHandler);

		InitState.initState(1, false, space, paintHandler);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				if (painterMode == "JPanel") {
					painter = new JPanelView(paintHandler); // chose either one, JPanel
				} else if (painterMode == "Canvas") {
					painter = new CanvasView(paintHandler); // or canvas
					painter.addKeyListener(input); // which needs key input
				}
				paintHandler.addPainter(painter);

				new MainWindow("PlaneSpace", painter, input);

				space.start(); // start the math
				paintHandler.startDisplay();
				
				
			}
		});

	}

}
