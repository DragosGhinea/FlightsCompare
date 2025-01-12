using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DXApplication1.Module.DTO
{
    public class FlightDto
    {
        public int Id { get; set; }
        public string Departure { get; set; }
        public string DepartureAbbreviation { get; set; }
        public string DepartureCity { get; set; }
        public string Arrival { get; set; }
        public string ArrivalAbbreviation { get; set; }
        public string ArrivalCity { get; set; }
        public DateTime DepartureDate { get; set; }
        public DateTime ArrivalDate { get; set; }
        public decimal Price { get; set; }
        public int FlightDuration { get; set; }
        public string PlaneCode { get; set; }
        public string Url { get; set; }
        public string YearValue { get; set; }
        public string MonthValue { get; set; }
        public string DayValue { get; set; }
    }

}
