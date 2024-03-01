package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEmployeeManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileEmployeeManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashEmployee> emplMap;

    // ---------------------------------------------------------------
    
	public FileEmployeeManager(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

    // ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		emplMap = new HashMap<GCshID, GnucashEmployee>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncEmployee) ) {
				continue;
			}
			GncGncEmployee jwsdpEmpl = (GncGncEmployee) bookElement;

			try {
				GnucashEmployeeImpl empl = createEmployee(jwsdpEmpl);
				emplMap.put(empl.getID(), empl);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Employee-Entry with id=" + jwsdpEmpl.getEmployeeId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in vendor map: " + emplMap.size());
	}

	protected GnucashEmployeeImpl createEmployee(final GncGncEmployee jwsdpEmpl) {
		GnucashEmployeeImpl empl = new GnucashEmployeeImpl(jwsdpEmpl, gcshFile);
		LOGGER.debug("Generated new employee: " + empl.getID());
		return empl;
	}

	// ---------------------------------------------------------------

	public void addEmployee(GnucashEmployee empl) {
		emplMap.put(empl.getID(), empl);
		LOGGER.debug("Added employee to cache: " + empl.getID());
	}

	public void removeEmployee(GnucashEmployee empl) {
		emplMap.remove(empl.getID());
		LOGGER.debug("Removed employee from cache: " + empl.getID());
	}

	// ---------------------------------------------------------------

	public GnucashEmployee getEmployeeByID(final GCshID id) {
		if ( emplMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashEmployee retval = emplMap.get(id);
		if ( retval == null ) {
			LOGGER.warn("getEmployeeByID: No Employee with id '" + id + "'. We know " + emplMap.size() + " employees.");
		}
		return retval;
	}

	public List<GnucashEmployee> getEmployeesByUserName(final String userName) {
		return getEmployeesByUserName(userName, true);
	}

	public List<GnucashEmployee> getEmployeesByUserName(final String expr, boolean relaxed) {

		if ( emplMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnucashEmployee> result = new ArrayList<GnucashEmployee>();

		for ( GnucashEmployee empl : getEmployees() ) {
			if ( relaxed ) {
				if ( empl.getUserName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
					result.add(empl);
				}
			} else {
				if ( empl.getUserName().equals(expr) ) {
					result.add(empl);
				}
			}
		}

		return result;
	}

	public GnucashEmployee getEmployeeByUserNameUniq(final String userName)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		List<GnucashEmployee> emplList = getEmployeesByUserName(userName);
		if ( emplList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( emplList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return emplList.get(0);
	}

	public Collection<GnucashEmployee> getEmployees() {
		return Collections.unmodifiableCollection(emplMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesCustomerMap() {
		return emplMap.size();
	}

}
