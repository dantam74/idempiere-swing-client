package org.compiere.grid;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.KeyNamePair;

public class EMUCurrencyCache {

	/**	Logger			*/
	protected transient CLogger log = CLogger.getCLogger(getClass());
	
	private Hashtable<Integer,KeyNamePair> s_Currencies = null;	
	
	public EMUCurrencyCache() {
		loadCurrencies();
	}
	
	public boolean containsKey(Integer key) {
		return s_Currencies.containsKey(key);
	}
	
	public Enumeration<Integer> keys() {
		return s_Currencies.keys();
	}
	
	public KeyNamePair get(Object key) {
		return s_Currencies.get(key);
	}
	
	/**
	 *	Fill s_Currencies with EMU currencies
	 */
	private void loadCurrencies()
	{
		s_Currencies = new Hashtable<Integer,KeyNamePair>(12);	//	Currenly only 10+1
		String SQL = "SELECT C_Currency_ID, ISO_Code FROM C_Currency "
			+ "WHERE (IsEMUMember='Y' AND EMUEntryDate<getDate()) OR IsEuro='Y' "
			+ "ORDER BY 2";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(SQL, null);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				int id = rs.getInt(1);
				String name = rs.getString(2);
				s_Currencies.put(Integer.valueOf(id), new KeyNamePair(id, name));
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, SQL, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	}	//	loadCurrencies
	
}
