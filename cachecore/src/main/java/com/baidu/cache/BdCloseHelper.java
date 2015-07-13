package com.baidu.cache;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BdCloseHelper {

	private static final String TAG = "adp_util_close";

	/**
	 * 关闭给定的输入流. <BR>
	 * 
	 * @param inStream
	 */
	public static void close(InputStream inStream) {
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				BdLog.e(TAG, "error on close the inputstream.", e.getMessage());
			}
		}
	}

	/**
	 * 关闭给定的流.
	 * 
	 * @param stream
	 */
	public static void close(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Throwable e) {
				BdLog.e(TAG, "error on close the Closeable.", e.getMessage());
			}
		}
	}

	/**
	 * 关闭给定的输出流. <BR>
	 * 
	 * @param outStream
	 */
	public static void close(OutputStream outStream) {
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {
				BdLog.e(TAG, "error on close the outputstream.", e.getMessage());
			}
		}
	}

	/**
	 * 关闭给定的输出流. <BR>
	 * 
	 * @param writer
	 */
	public static void close(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				BdLog.e(TAG, "error on close the outputstream.", e.getMessage());
			}
		}
	}

	/**
	 * 关闭给定的Socket.
	 * 
	 * @param socket
	 *            给定的Socket
	 */
	public static void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				BdLog.e(TAG, "fail on close socket: " + socket, e.getMessage());
			}
		}
	}

	public static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				BdLog.e(TAG, "error on close the Reader.", e.getMessage());
			}
		}
	}

	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				BdLog.e(TAG, "error on close java.sql.Connection.", e.getMessage());
			}
		}
	}

	public static void close(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (Exception e) {
				BdLog.e(TAG, "error on close java.sql.PreparedStatement.", e.getMessage());
			}
		}
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				BdLog.e(TAG, "error on close java.sql.ResultSet.", e.getMessage());
			}
		}
	}

	public static void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				BdLog.e(TAG, "error on close java.sql.Statement.", e.getMessage());
			}
		}
	}

	public static void close(Cursor c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				BdLog.e(TAG, "error on close android.database.Cursor.", e.getMessage());
			}
		}
	}

	public static void close(SQLiteDatabase db) {
		if (db != null) {
			try {
				db.close();
			} catch (Exception e) {
				BdLog.e(TAG, "error on close android.database.SQLiteDatabase.", e.getMessage());
			}
		}
	}

	public static void close(HttpURLConnection mConn) {
		if (mConn != null) {
			try {
				mConn.disconnect();
			} catch (Exception e) {
				BdLog.e(TAG, "error on close HttpURLConnection.", e.getMessage());
			}
		}
	}

}
