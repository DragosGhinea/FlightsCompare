using DevExpress.Data.Filtering;
using DevExpress.ExpressApp;
using DevExpress.ExpressApp.DC;
using DevExpress.ExpressApp.Model;
using DevExpress.Persistent.Base;
using DevExpress.Persistent.BaseImpl;
using DevExpress.Persistent.Validation;
using DevExpress.Xpo;
using System;

namespace FlightsCompare.Module.BusinessObjects
{
    [DefaultClassOptions]
    public class Zbor : BaseObject
    {
        public Zbor(Session session) : base(session)
        {
        }

        public override void AfterConstruction()
        {
            base.AfterConstruction();
            // Place your initialization code here
        }

        private string orasPlecare;
        private string orasDestinatie;
        private DateTime dataPlecare;
        private DateTime dataSosire;
        private decimal pret;
        private int durataZbor;
        private string codAvion;
        private string linkWebsite;

        public string OrasPlecare
        {
            get => orasPlecare;
            set => SetPropertyValue(nameof(OrasPlecare), ref orasPlecare, value);
        }

        public string OrasDestinatie
        {
            get => orasDestinatie;
            set => SetPropertyValue(nameof(OrasDestinatie), ref orasDestinatie, value);
        }

        public DateTime DataPlecare
        {
            get => dataPlecare;
            set => SetPropertyValue(nameof(DataPlecare), ref dataPlecare, value);
        }

        public DateTime DataSosire
        {
            get => dataSosire;
            set => SetPropertyValue(nameof(DataSosire), ref dataSosire, value);
        }

        public decimal Pret
        {
            get => pret;
            set => SetPropertyValue(nameof(Pret), ref pret, value);
        }

        public int DurataZbor
        {
            get => durataZbor;
            set => SetPropertyValue(nameof(DurataZbor), ref durataZbor, value);
        }

        public string CodAvion
        {
            get => codAvion;
            set => SetPropertyValue(nameof(CodAvion), ref codAvion, value);
        }

        public string LinkWebsite
        {
            get => linkWebsite;
            set => SetPropertyValue(nameof(LinkWebsite), ref linkWebsite, value);
        }
    }
}
