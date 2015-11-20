import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class ZipfGraph extends ApplicationFrame {

	public ZipfGraph(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void drawZipfCurve( //plots Zipf curve on sortedWordFrequency
			LinkedHashMap<String, Long> sortedWordFrequency, int totalFrequency1)
			throws IOException {
		final XYSeries z = new XYSeries("Zipf Curve");
		Iterator<String> it = sortedWordFrequency.keySet().iterator();
		String word;
		long freq;
		Double probability = 0.0;
		int rank = 0;
		while (it.hasNext()) {
			word = it.next();
			freq = sortedWordFrequency.get(word);
			probability = ((double) freq / (double) totalFrequency1);
			z.add(++rank, probability);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(z);
		JFreeChart xylineChart = ChartFactory.createXYLineChart(
				"Rank V/S Probability", "Rank", "Probability", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		int width = 1000; /* Width of the image */
		int height = 1000; /* Height of the image */
		File XYChart = new File("ZipfCurve.jpeg");
		if (!XYChart.exists()) {
			XYChart.createNewFile();
		}
		ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);

	}

}
