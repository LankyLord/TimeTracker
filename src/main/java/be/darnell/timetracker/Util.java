/*
 * Copyright (c) 2013 cedeel.
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package be.darnell.timetracker;

public class Util {

    /**
     * Construct a human readable string of a duration
     * @param start Beginning time in seconds.
     * @param end End time in seconds
     * @return A human readable string of a duration
     */
    public static String humanTime(long start, long end) {
        if (start != -1L) {
            final long finalTime = (end - start) / 1000L;
            final long MINUTE = 60L;
            final long HOUR = 60*MINUTE;
            final long DAY = 24*HOUR;
            final long MONTH = 30*DAY;
            final long YEAR = 365*DAY;

            if (finalTime >= YEAR) {
                String s = (finalTime >= (2*YEAR)) ? "years" : "year";
                return (finalTime / YEAR + " " + s);
            } else if ( finalTime >= MONTH) {
                String s = (finalTime >= (2*MONTH)) ? "months" : "month";
                return (finalTime / MONTH + " " + s);
            } else if (finalTime >= DAY) {
                String s = (finalTime >= (2*DAY)) ? "days" : "day";
                return (finalTime / DAY + " " + s);
            } else if (finalTime >= HOUR) {
                String s = (finalTime >= (2*HOUR)) ? "hours" : "hour";
                return (finalTime / HOUR + " " + s);
            } else if (finalTime >= MINUTE) {
                String s = (finalTime >= (2*MINUTE)) ? "minutes" : "minute";
                return (finalTime / MINUTE + " " + s);
            } else {
                String s = (finalTime >= 2) ? "seconds" : "second";
                return (finalTime + " " + s);
            }
        }
        return null;
    }

    public final static long UNINITIALISED_TIME = -1L;
}
