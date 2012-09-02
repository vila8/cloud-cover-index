import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Calcula el índice de nubes de la zona sur de la Ciudad de México mediante imágenes obtenidas por la facultad de ciencias, UNAM.
 * @version Septiembre 2012
 * @author Jesús Vila
 */
public class CloudCoverage {

	private BufferedImage imagen;
	private final int CENTROX = 2184;
	private final int CENTROY = 1456;
	private final int RADIO = 1350;
	private String nombreImagen;

	public CloudCoverage(String nombreImagen)	{
		try {
			imagen = ImageIO.read(new File(nombreImagen));
			this.nombreImagen = nombreImagen;
		}catch(Exception e){
			System.err.println("I/O Error");
			e.printStackTrace(System.err);
		}
	}
	private double distancia(int x, int y) {
		double d = Math.pow(-RADIO+(x-(CENTROX-RADIO)), 2) + Math.pow(RADIO-(y-(CENTROY-RADIO)), 2);
		return Math.pow(d, (.5));
	}

	public void segmentacion(String segmentado) {
		BufferedImage imagenSegmentada = new BufferedImage(RADIO*2, RADIO*2,BufferedImage.TYPE_4BYTE_ABGR);

		double ratio;
		final double threshold = 0.95;
		double area = 0;
		int numeroNubes = 0;

		double area2 = Math.PI*(RADIO*RADIO);
		Color colorPixel;

		for (int i = (CENTROX - RADIO); i < (CENTROX + RADIO); i++){
			for (int j = (CENTROY - RADIO); j < (CENTROY + RADIO); j++){

				colorPixel = new Color(imagen.getRGB(i,j));
				if (distancia(i, j) <= RADIO)	{

				double rojo = colorPixel.getRed();
				double azul = colorPixel.getBlue();

				ratio = rojo/azul;
					if (ratio< threshold) {
						colorPixel = Color.BLACK;
						imagenSegmentada.setRGB(i-(CENTROX-RADIO), j-(CENTROY-RADIO), colorPixel.getRGB());
					} else{
						colorPixel = Color.WHITE;
						imagenSegmentada.setRGB(i-(CENTROX-RADIO), j-(CENTROY-RADIO), colorPixel.getRGB());
						numeroNubes++;
					}
				}

			}
		}

		System.out.println("EL CCI es: " + (numeroNubes/area2));

		try {
			if (segmentado.equalsIgnoreCase("s")){
				System.out.println("Guardando imagen segmentada: " + nombreImagen);
				this.nombreImagen = nombreImagen.substring(0,nombreImagen.indexOf("."));
				ImageIO.write(imagenSegmentada, "PNG", new File(nombreImagen + "-seg.png"));
				System.out.printf("La imagen %s se ha guardado correctamente\n",nombreImagen + "-seg.png");
			}
		}catch(IOException e){
			System.err.println("I/O Error");
			e.printStackTrace(System.err);
		}
	}

	public static void main(String [] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException("El numero de parametros debe ser al menos 1");
		} else{
			System.out.println("Leyendo imagen: " + args[0]);
			if (args.length  == 2){
				CloudCoverage cloud = new CloudCoverage(args[0]);
				cloud.segmentacion(args[1]);
			} else {
				CloudCoverage cloud = new CloudCoverage(args[0]);
				cloud.segmentacion(args[0]);
			}
		}
	}
}
