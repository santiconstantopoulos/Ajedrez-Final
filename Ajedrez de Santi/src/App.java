import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class App {


	

	static int cont = 2;

	public static String generarPosicion(int i, int j) {
		// Genera la posicion a guardar dentro del tablero de ajedrez. Example: A-2
		return ((char) (i + 65) + String.valueOf(j));
	}

	public static Pieza procesarPieza(String tipoPieza, String color, Map map) {
		Pieza pieza = null;
		switch (tipoPieza) {

			case "Rey":
				return color == "Negro" ? new Rey("Rey", "Tenue", "Postrero", color, generarPosicion(4, 1)) :

						new Rey("Rey", "Tenue", "Postrero", color, generarPosicion(4, 8));

			case "Reina":
				return color == "Negro" ? new Reina("Reina", "Armada", "Encarnizada", color, generarPosicion(3, 1)) :

						new Reina("Reina", "Armada", "Encarnizada", color, generarPosicion(3, 8));

			case "Alfil":
				if (color == "Negro") {
					if (cont % 2 == 0) {
						cont++;
						return new Alfil("Alfil", "Oblicuo", "", color, generarPosicion(2, 1));
					} else {
						cont++;
						return new Alfil("Alfil", "Oblicuo", "", color, generarPosicion(5, 1));
					}

				} else {
					if (cont % 2 == 0) {
						cont++;
						return new Alfil("Alfil", "Oblicuo", "Sesgo", color, generarPosicion(2, 8));
					} else {
						cont++;
						return new Alfil("Alfil", "Oblicuo", "Sesgo", color, generarPosicion(5, 8));
					}
				}

			case "Caballo":
				if (color == "Negro") {
					if (cont % 2 == 0) {
						cont++;
						return new Caballo("Caballo", "Ligero", "", color, generarPosicion(1, 1));
					} else {
						cont++;
						return new Caballo("Caballo", "Ligero", "", color, generarPosicion(6, 1));
					}

				} else {
					if (cont % 2 == 0) {
						cont++;
						return new Caballo("Caballo", "Ligero", "", color, generarPosicion(1, 8));
					} else {
						cont++;
						return new Caballo("Caballo", "Ligero", "", color, generarPosicion(6, 8));
					}
				}

			case "Torre":
				if (color == "Negro") {
					if (cont % 2 == 0) {
						cont++;
						return new Torre("Torre", "Directa", "Homerica", color, generarPosicion(0, 1));
					} else {
						cont++;
						return new Torre("Torre", "Directa", "Homerica", color, generarPosicion(7, 1));
					}
				} else {
					if (cont % 2 == 0) {
						cont++;
						return new Torre("Torre", "Directa", "Homerica", color, generarPosicion(0, 8));
					} else {
						cont++;
						return new Torre("Torre", "Directa", "Homerica", color, generarPosicion(7, 8));
					}
				}

			case "Peon":
				if (color == "Negro") {
					for (int i = 0; i < 8; i++) {
						pieza = new Peon("Peon", "Ladinos", "Agresores", color, generarPosicion(i, 2));
						almacenarPieza(pieza, map);
					}
				} else {
					for (int i = 0; i < 8; i++) {
						pieza = new Peon("Peon", "Ladinos", "Agresores", color, generarPosicion(i, 7));
						almacenarPieza(pieza, map);

					}
				}
			default:
				break;

		}

		return pieza;

	}

	public static void crearTablero() {
		new Tablero();
	}

	public static void almacenarPieza(Pieza mipieza, Map map) {


		// Objeto para ejecutar el alta/actualizacion en la base de datos
		AccesoDatos accesoBD = null;
		Connection con = null;
		PreparedStatement sentencia = null;

		try {
			// Instancio un objeto de acceso a datos
			accesoBD = new AccesoDatos("localhost","root", "Santi!", 3306, "Ajedrez");
			// Obtener la conexion para poder generar la sentencia de consulta
			con = accesoBD.getConexion();

			
			String insertScript = "INSERT INTO pieza"
					+ "(Descripcion, idMaterial, idColor, idTamanio, idTipoPieza, Posicion, Capacidad_Desplazamiento, Conducta)"
					+ " VALUES(?,?,?,?,?,?,?,?)";
			sentencia = con.prepareStatement(insertScript);

			sentencia.setString(1, mipieza.getNombrePieza() + " " + mipieza.getCapacidadDesplazamiento() + " y "
					+ mipieza.getConducta());
			sentencia.setInt(2, 1);
			sentencia.setString(3, mipieza.getColor()); // negro 2 blanco 1
			sentencia.setInt(4, 1);
			sentencia.setInt(5, map.get(mipieza.getNombrePieza()));
			sentencia.setString(6, mipieza.getPosicion());
			sentencia.setString(7, mipieza.getCapacidadDesplazamiento());
			sentencia.setString(8, mipieza.getConducta());



			// execute insert SQL statement
			sentencia.executeUpdate();

		} catch (SQLException error) {
			System.err.println("Error al insertar la pieza.");
			error.printStackTrace();
		} finally {
			try {
				// Cierra la sentencia
				if (sentencia != null)
					sentencia.close();
				// Cierra la conexion
				if (con != null)
					con.close();

			} catch (SQLException error) {
				System.err.println("Error al cerrar conexion");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
		Map<String, Integer> map = new HashMap<String, Integer>();

		map.put("Reina", 1);
		map.put("Rey", 2);
		map.put("Torre", 3);
		map.put("Alfil", 4);
		map.put("Caballo", 5);
		map.put("Peon", 6);

		/*
		 * REFERENCIA DE IDTIPOPIEZA
		 * # idTipoPieza Descripciondepieza
		 * 1 Reina
		 * 2 Rey
		 * 3 Torre
		 * 4 Alfil
		 * 5 Caballo
		 * 6 Peon
		 */
		System.out.println("Bienvenido al Programa de Ajedrez.");
		crearTablero();

		almacenarPieza(procesarPieza("Rey", "Negro"), map);
		almacenarPieza(procesarPieza("Rey", "Blanco"), map);

		almacenarPieza(procesarPieza("Reina", "Negro"), map);
		almacenarPieza(procesarPieza("Reina", "Blanco"), map);
		almacenarPieza(procesarPieza("Alfil", "Negro"), map);
		almacenarPieza(procesarPieza("Alfil", "Negro"), map);
		almacenarPieza(procesarPieza("Alfil", "Blanco"), map);
		almacenarPieza(procesarPieza("Alfil", "Blanco"), map);
		almacenarPieza(procesarPieza("Caballo", "Negro"), map);
		almacenarPieza(procesarPieza("Caballo", "Negro"), map);
		almacenarPieza(procesarPieza("Caballo", "Blanco"), map);
		almacenarPieza(procesarPieza("Caballo", "Blanco"), map);
		almacenarPieza(procesarPieza("Torre", "Negro"), map);
		almacenarPieza(procesarPieza("Torre", "Negro"), map);
		almacenarPieza(procesarPieza("Torre", "Blanco"), map);
		almacenarPieza(procesarPieza("Torre", "Blanco"), map);
		procesarPieza("Peon", "Negro", map);
		procesarPieza("Peon", "Blanco", map);
	}

}
