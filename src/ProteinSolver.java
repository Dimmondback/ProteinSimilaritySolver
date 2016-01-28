import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.JRadioButton;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

public class ProteinSolver {
	static JFrame frame1 = new JFrame("Protein Similarity Solver");
	static JTextArea arrayText = new JTextArea();
	static JTextPane resultArea = new JTextPane();
	static JButton submit = new JButton("Submit");
	static Border border1 = new LineBorder(Color.GRAY);
	static JRadioButton radio1 = new JRadioButton("Chrome");
	static JRadioButton radio2 = new JRadioButton("Firefox");
	static ButtonGroup buttons = new ButtonGroup();
	static WebDriver driver;
	static Actions tabber;

	public static void newTab() {
		tabber.sendKeys(Keys.CONTROL + "t").perform();
		for (String tabbings : driver.getWindowHandles()) {
			driver.switchTo().window(tabbings);
		}
	}

	private static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e1) {
		}
	}

	public static void search() {
		driver.get("http://swissmodel.expasy.org/interactive");
		sleep(1);
		driver.findElement(By.xpath("//*[@id='id_target']")).sendKeys(
				arrayText.getText());
		sleep(3);
		driver.findElement(
				By.xpath("//*[@id='buildButton' and @type='button']")).click();
		newTab();
		driver.get("http://blast.ncbi.nlm.nih.gov/Blast.cgi?PROGRAM=blastp&PAGE_TYPE=BlastSearch&LINK_LOC=blasthome");
		driver.findElement(By.xpath("//*[@id='seq']")).sendKeys("");
		driver.findElement(By.xpath("//*[@id='seq']")).sendKeys(
				arrayText.getText());
		sleep(1);
		driver.findElement(By.xpath("//*[@id='algPar']/span[2]")).click();
		driver.findElement(By.xpath("//*[@id='expect' and @type='text']"))
				.sendKeys(Keys.BACK_SPACE + "" + Keys.BACK_SPACE + "5" + "0");
		driver.findElement(By.xpath("//*[@id='b2']")).click();
		sleep(1);
		while (true) {
			try {
				driver.findElement(By.xpath("//*[@class='dflSeq']"))
						.isEnabled();
				break;
			} catch (StaleElementReferenceException e1) {
			} catch (WebDriverException e2) {
			} catch (NullPointerException e3) {
			}
		}
		List<WebElement> links = driver.findElements(By
				.xpath("//*[@class='dflSeq']"));
		ArrayList<String> strings = new ArrayList<String>();
		List<WebElement> fastas;
		int i = 0;
		if (links.size() > 0) {
			for (WebElement it : links) {
				try {
					strings.add(it.getAttribute("href").toString());
				} catch (NullPointerException e1) {
				}
			}
			for (int j = 0; j < strings.size(); j++) {
				if (j < 3) {
					newTab();
					driver.get(strings.get(j));
					driver.findElement(By.xpath("//*[@id='ReportShortCut6']"))
							.click();
					driver.findElement(By.cssSelector("body")).sendKeys(
							Keys.CONTROL + "t");
					fastas = driver.findElements(By
							.xpath("//*[@class='ff_line']"));
					String temp = "";
					for (WebElement it : fastas) {
						temp = temp + it.getText();
					}
					driver.get("http://swissmodel.expasy.org/interactive");
					sleep(2);
					driver.findElement(By.xpath("//*[@id='id_target']"))
							.sendKeys(temp);
					sleep(3);
					try {
						driver.findElement(
								By.xpath("//*[@id='buildButton' and @type='button']"))
								.click();
					} catch (org.openqa.selenium.WebDriverException noclick) {
						continue;
					}
				}
			}
		}
	}

	public static void windowSetup() {
		frame1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame1.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					driver.quit();
					System.exit(1);
				} catch (WebDriverException error1) {
					System.exit(1);
				} catch (NullPointerException error2) {
					System.exit(1);
				}
			}
		});
		frame1.setResizable(false);
		frame1.setLayout(null);
		frame1.setBounds((int) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth()
				/ 8 - frame1.getWidth() / 2, (int) Toolkit.getDefaultToolkit()
				.getScreenSize().getHeight()
				/ 8 - frame1.getHeight() / 2, 900, 400);
		arrayText.setBounds(1, 1, (3 * frame1.getWidth()) / 4 - 3,
				frame1.getHeight() - 30);
		arrayText.setBorder(border1);
		arrayText.setLineWrap(true);
		arrayText.setWrapStyleWord(false);
		resultArea.setBounds(arrayText.getWidth() + 2, 1, frame1.getWidth()
				- arrayText.getWidth() - 9, arrayText.getHeight() - 62);
		resultArea.setBorder(border1);
		resultArea.setFocusable(false);
		resultArea
				.setText("Please paste your sequence to the left.\n\n\nPlease do not paste the line containing the name/descriptor (usually starts with a \">\").\n\n\nPlease choose either Chrome or Firefox for your browser.");
		radio1.setBounds(resultArea.getX(), resultArea.getHeight() + 1, 100, 30);
		radio2.setBounds(radio1.getX() + radio1.getWidth() + 1, radio1.getY(),
				100, 30);
		buttons.add(radio1);
		buttons.add(radio2);
		submit.setBounds(resultArea.getX(), resultArea.getHeight() + 33,
				resultArea.getWidth(), 30);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Chrome VS Firefox goes here!
				if (radio1.isSelected()) {
					driver = new ChromeDriver();
				} else if (radio2.isSelected()) {
					driver = new FirefoxDriver();
				} else {
					return;
				}
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				tabber = new Actions(driver);
				search();
			}

		});
		frame1.add(arrayText);
		frame1.add(resultArea);
		frame1.add(submit);
		frame1.add(radio1);
		frame1.add(radio2);
		frame1.setVisible(true);
	}

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		System.setProperty("webdriver.firefox.driver", "firefoxdriver.exe");
		windowSetup();
	}
}