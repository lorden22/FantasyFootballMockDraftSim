import { lazy, Suspense } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import { AnimatePresence } from "motion/react";
import { GuestOnly, RequireAuth } from "./auth/RequireAuth";
import { FullPageLoader } from "./components/FullPageLoader";
import { LandingPage } from "./features/landing/LandingPage";
import { LoginPage } from "./features/auth/LoginPage";
import { RegisterPage } from "./features/auth/RegisterPage";
import { ForgotPasswordPage } from "./features/auth/ForgotPasswordPage";
import { ResetPasswordPage } from "./features/auth/ResetPasswordPage";
import { NotFoundPage } from "./features/NotFoundPage";

const DashboardPage = lazy(() => import("./features/dashboard/DashboardPage"));
const DraftRoomPage = lazy(() => import("./features/draft/DraftRoomPage"));
const DraftReviewPage = lazy(() => import("./features/review/DraftReviewPage"));
const AccountPage = lazy(() => import("./features/account/AccountPage"));

export function App() {
  const location = useLocation();
  return (
    <Suspense fallback={<FullPageLoader />}>
      <AnimatePresence mode="wait" initial={false}>
        <Routes location={location} key={location.pathname}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<GuestOnly><LoginPage /></GuestOnly>} />
          <Route path="/register" element={<GuestOnly><RegisterPage /></GuestOnly>} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route path="/dashboard" element={<RequireAuth><DashboardPage /></RequireAuth>} />
          <Route path="/drafts/:id" element={<RequireAuth><DraftRoomPage /></RequireAuth>} />
          <Route path="/drafts/:id/review" element={<RequireAuth><DraftReviewPage /></RequireAuth>} />
          <Route path="/account" element={<RequireAuth><AccountPage /></RequireAuth>} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </AnimatePresence>
    </Suspense>
  );
}
