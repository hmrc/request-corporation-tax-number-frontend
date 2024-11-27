import forms.CompanyDetailsFormProvider
import models.{CompanyDetails, Mode, NormalMode}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.api.data.Forms.{boolean, text}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.scalatestaccessibilitylinter.views.AutomaticAccessibilitySpec
import views.html._

class FrontendAccessibilitySpec extends AutomaticAccessibilitySpec {

  private val companyDetailsForm: Form[CompanyDetails] = new CompanyDetailsFormProvider().apply()
  private val booleanForm: Form[Boolean] = Form("value" -> boolean)
  private val stringForm: Form[String] = Form("value" -> text)

  implicit val arbForm: Arbitrary[Form[_]] = fixed(booleanForm)
  implicit val arbString: Arbitrary[String] = fixed("http://something")
  implicit val arbFormString: Arbitrary[Form[String]] = fixed(stringForm)
  implicit val arbCompanyDetailsForm: Arbitrary[Form[CompanyDetails]] = fixed(companyDetailsForm)
  implicit val arbMode: Arbitrary[Mode] = fixed(NormalMode)
  implicit val arbRequest1: Arbitrary[RequestHeader] = fixed(fakeRequest)

  val viewPackageName = "views.html"

  val layoutClasses: Seq[Class[MainTemplate]] = Seq(classOf[views.html.MainTemplate])


  override def renderViewByClass: PartialFunction[Any, Html] = {
    case checkYourAnswersView: CheckYourAnswersView => render(checkYourAnswersView)
    case companyDetailsNoMatchView: CompanyDetailsNoMatchView => render(companyDetailsNoMatchView)
    case companyDetailsView: CompanyDetailsView => render(companyDetailsView)
    case confirmationView: ConfirmationView => render(confirmationView)
    case errorTemplateInternalServerErrorView: ErrorTemplateInternalServerErrorView => render(errorTemplateInternalServerErrorView)
    case errorTemplateNotFoundView: ErrorTemplateNotFoundView => render(errorTemplateNotFoundView)
    case error_template: ErrorTemplateView => render(error_template)
    case failedToSubmitView: FailedToSubmitView => render(failedToSubmitView)
    case indexView: IndexView => render(indexView)
    case sessionExpiredView: SessionExpiredView => render(sessionExpiredView)
    case companyRegisteredView: CompanyRegisteredView => render(companyRegisteredView)
  }

  runAccessibilityTests()
}
