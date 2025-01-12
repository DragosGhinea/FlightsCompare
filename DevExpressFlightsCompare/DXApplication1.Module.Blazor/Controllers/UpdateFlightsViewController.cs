using DevExpress.Data.Filtering;
using DevExpress.ExpressApp;
using DevExpress.ExpressApp.Actions;
using DevExpress.ExpressApp.Editors;
using DevExpress.ExpressApp.Layout;
using DevExpress.ExpressApp.Model.NodeGenerators;
using DevExpress.ExpressApp.SystemModule;
using DevExpress.ExpressApp.Templates;
using DevExpress.ExpressApp.Utils;
using DevExpress.Persistent.Base;
using DevExpress.Persistent.Validation;
using DXApplication1.Module.DTO;
using FlightsCompare.Module.BusinessObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace DXApplication1.Module.Blazor.Controllers
{
    // For more typical usage scenarios, be sure to check out https://documentation.devexpress.com/eXpressAppFramework/clsDevExpressExpressAppViewControllertopic.aspx.
    public partial class UpdateFlightsViewController : ViewController
    {
        private SimpleAction updateFlightsAction;

        public UpdateFlightsViewController()
        {
            // This controller targets the Zbor ListView 
            TargetObjectType = typeof(Zbor);
            TargetViewNesting = Nesting.Root;

            // Create a SimpleAction
            updateFlightsAction = new SimpleAction(
                this,
                "UpdateFlights",
                PredefinedCategory.Edit
            )
            {
                Caption = "Update Flights", // Button label
                ImageName = "Refresh"       // Optional icon
            };

            // Attach the Execute event handler
            updateFlightsAction.Execute += UpdateFlightsAction_Execute;

            // Register the action
            Actions.Add(updateFlightsAction);
        }

        private async void UpdateFlightsAction_Execute(object sender, SimpleActionExecuteEventArgs e)
        {
            // 1. Call the API and retrieve the flight data
            var flightDtos = await GetFlightsFromApi();

            if (flightDtos == null || flightDtos.Count == 0)
            {
                Application.ShowViewStrategy.ShowMessage(
                    "No flights received from the API.",
                    InformationType.Info,
                    4000,
                    InformationPosition.Bottom
                );
                return;
            }

            // 2. Process each FlightDto and create/update Zbor + Oras
            int createdCount = 0;
            foreach (var dto in flightDtos)
            {
                // Check if there's already a Zbor with this OldId
                Zbor existingZbor = ObjectSpace
                    .FindObject<Zbor>(new DevExpress.Data.Filtering.BinaryOperator(nameof(Zbor.OldId), dto.Id));

                if (existingZbor == null)
                {
                    // Create a new Zbor
                    existingZbor = ObjectSpace.CreateObject<Zbor>();
                    existingZbor.OldId = dto.Id;
                    createdCount++;
                }

                // Fill data from DTO
                existingZbor.DataPlecare = dto.DepartureDate;
                existingZbor.DataSosire = dto.ArrivalDate;
                existingZbor.Pret = dto.Price;
                existingZbor.DurataZbor = dto.FlightDuration;
                existingZbor.CodAvion = dto.PlaneCode;
                existingZbor.LinkWebsite = dto.Url;

                // Handle OrasPlecare
                var orasPlecare = FindOrCreateOras(dto.DepartureCity, dto.DepartureAbbreviation);
                existingZbor.OrasPlecare = orasPlecare;

                // Handle OrasDestinatie
                var orasDestinatie = FindOrCreateOras(dto.ArrivalCity, dto.ArrivalAbbreviation);
                existingZbor.OrasDestinatie = orasDestinatie;
            }

            // Commit changes to the database
            ObjectSpace.CommitChanges();

            // 3. Show a confirmation message
            Application.ShowViewStrategy.ShowMessage(
                $"Updated/Created {createdCount} new flight(s).",
                InformationType.Success,
                5000,
                InformationPosition.Bottom
            );

            // Optionally refresh the current ListView
            View.ObjectSpace.Refresh();
        }

        private Oras FindOrCreateOras(string denumire, string cod)
        {
            // Search for an existing Oras by cod
            var existingOras = ObjectSpace
                .FindObject<Oras>(new DevExpress.Data.Filtering.BinaryOperator(nameof(Oras.Cod), cod));

            if (existingOras == null)
            {
                // Create a new Oras
                existingOras = ObjectSpace.CreateObject<Oras>();
                existingOras.Cod = cod;
                existingOras.Denumire = denumire;
            }
            else
            {
                // Update the Denumire if needed (optional)
                existingOras.Denumire = denumire;
            }

            return existingOras;
        }

        /// <summary>
        /// Retrieves flight data from the remote API endpoint.
        /// </summary>
        /// <returns>List of FlightDto objects or empty list if none.</returns>
        private async Task<System.Collections.Generic.List<FlightDto>> GetFlightsFromApi()
        {
            string url = "https://footballmanagergame-771c01868d32.herokuapp.com/flight/allFlights/1000";
            try
            {
                using (HttpClient client = new HttpClient())
                {
                    // Option A: Using HttpClient + System.Text.Json
                    var responseString = await client.GetStringAsync(url);

                    var options = new JsonSerializerOptions
                    {
                        PropertyNameCaseInsensitive = true
                    };

                    var list = JsonSerializer.Deserialize<System.Collections.Generic.List<FlightDto>>(responseString, options);
                    return list ?? new System.Collections.Generic.List<FlightDto>();

                    // Option B (if you're on .NET 5+ and have System.Net.Http.Json):
                    // return await client.GetFromJsonAsync<List<FlightDto>>(url);
                }
            }
            catch (Exception ex)
            {
                // Handle exceptions (network issues, JSON parse errors, etc.)
                Application.ShowViewStrategy.ShowMessage(
                    "Error calling flights API: " + ex.Message,
                    InformationType.Error,
                    5000,
                    InformationPosition.Bottom
                );
                return new System.Collections.Generic.List<FlightDto>();
            }
        }
    }
}
