using DevExpress.Data.Filtering;
using DevExpress.ExpressApp;
using DevExpress.ExpressApp.DC;
using DevExpress.ExpressApp.Model;
using DevExpress.Persistent.Base;
using DevExpress.Persistent.BaseImpl;
using DevExpress.Persistent.Validation;
using DevExpress.Xpo;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;

namespace FlightsCompare.Module.BusinessObjects
{
    [DefaultClassOptions]
    [DefaultProperty(nameof(Denumire))]
    public class Oras : BaseObject
    {
        public Oras(Session session)
            : base(session)
        {
        }
        public override void AfterConstruction()
        {
            base.AfterConstruction();
            // Place your initialization code here (https://documentation.devexpress.com/eXpressAppFramework/CustomDocument112834.aspx).
        }

        string cod;
        string denumire;

        [Size(SizeAttribute.DefaultStringMappingFieldSize)]
        public string Denumire
        {
            get => denumire;
            set => SetPropertyValue(nameof(Denumire), ref denumire, value);
        }

        [Size(SizeAttribute.DefaultStringMappingFieldSize)]
        public string Cod
        {
            get => cod;
            set => SetPropertyValue(nameof(Cod), ref cod, value);
        }
    }
}