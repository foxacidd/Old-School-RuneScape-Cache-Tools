/*
 * (C) Copyright IBM Corp. 2005, 2008
 * All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package synth.modules;

/**
 * A description class for direct MIDI input devices.
 * 
 * @author florian
 */
public class DirectMidiInDeviceEntry {

	private String devName;
	private String driverName;

	
	/**
	 * 
	 * @param devName
	 * @param driverName
	 */
	public DirectMidiInDeviceEntry(String devName, String driverName) {
		this.devName = devName;
		this.driverName = driverName;
	}



	/**
	 * @return the devName
	 */
	public String getDevName() {
		return devName;
	}



	/**
	 * @return the name
	 */
	public String getDriverName() {
		return driverName;
	}

	private String getPaddedDevName(int length) {
		StringBuffer result = new StringBuffer(devName);
		while (result.length() < length) {
			result.append(" ");
		}
		return result.toString();
	}
	
	public String getFullInfoString() {
		return getPaddedDevName(12)+": "+getDriverName();
	}

	public String toString() {
		return getDevName()+"|"+getDriverName();
	}

}
