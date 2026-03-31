import { useEffect, useMemo, useState } from "react";
import ProductCard from "../components/ProductCard";

const API_BASE = "http://localhost:8080/api";
const emptyCheckout = { customerName: "", email: "", address: "" };

function HomePage({ session, onLogout, theme, toggleTheme }) {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [orders, setOrders] = useState([]);
  const [showSearch, setShowSearch] = useState(false);
  const [showExplore, setShowExplore] = useState(true);
  const [search, setSearch] = useState("");
  const [checkoutForm, setCheckoutForm] = useState({
    ...emptyCheckout,
    customerName: session.fullName || "",
    email: session.username || "",
  });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadProducts();
    loadCart();
    loadOrders();
  }, []);

  const productLookup = useMemo(
    () => Object.fromEntries(products.map((product) => [product.id, product])),
    [products]
  );

  const catalogItems = useMemo(() => {
    return products
      .filter((product) => {
        const term = search.trim().toLowerCase();
        return (
          !term ||
          product.name.toLowerCase().includes(term) ||
          product.category.toLowerCase().includes(term) ||
          product.description.toLowerCase().includes(term)
        );
      })
      .map((product) => {
        const cartItem = cart.find((item) => item.productId === product.id);
        return { product, quantity: cartItem?.quantity || 0 };
      });
  }, [products, cart, search]);

  async function readJson(response) {
    return response.status === 204 ? null : response.json();
  }

  async function request(url, options = {}) {
    const response = await fetch(url, options);
    const data = await readJson(response);
    if (!response.ok) {
      throw new Error(data?.message || "Something went wrong.");
    }
    return data;
  }

  async function loadProducts() {
    try {
      setProducts(await request(`${API_BASE}/products`));
    } catch {
      setError("Could not load products.");
    }
  }

  async function loadCart() {
    try {
      setCart(await request(`${API_BASE}/cart/${session.userId}`));
    } catch {
      setError("Could not load cart.");
    }
  }

  async function loadOrders() {
    try {
      setOrders(await request(`${API_BASE}/order/${session.userId}`));
    } catch {
      setError("Could not load orders.");
    }
  }

  async function buyProduct(productId) {
    try {
      await request(`${API_BASE}/cart/add`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId: session.userId, productId, quantity: 1 }),
      });
      await loadCart();
      setMessage("Product added to cart.");
      setError("");
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function cancelProduct(productId) {
    try {
      await request(`${API_BASE}/cart/${session.userId}/items/${productId}`, {
        method: "DELETE",
      });
      await loadCart();
      setMessage("Product removed from cart.");
      setError("");
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function checkout(event) {
    event.preventDefault();
    if (cart.length === 0) {
      setError("Add products to cart first.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await request(`${API_BASE}/order/place`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userId: session.userId,
          customerName: checkoutForm.customerName,
          email: checkoutForm.email,
          address: checkoutForm.address,
        }),
      });
      await Promise.all([loadProducts(), loadCart(), loadOrders()]);
      setCheckoutForm((current) => ({ ...current, address: "" }));
      setMessage("Order placed successfully.");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  const cartTotal = cart.reduce((sum, item) => {
    const product = productLookup[item.productId];
    return product ? sum + product.price * item.quantity : sum;
  }, 0);

  return (
    <main className="home-page">
      <header className="home-header">
        <div>
          <p className="eyebrow">Home Page</p>
          <h1>Welcome, {session.fullName || session.username}</h1>
          <p className="subtext">Welcome to our Union E-commerce Website ,Enjoy your purchasing</p>
        </div>
        <div className="button-row">
          <button className="ghost-button" onClick={() => setShowSearch((current) => !current)} type="button">
            Search
          </button>
          <button className="ghost-button" onClick={() => setShowExplore((current) => !current)} type="button">
            Explore
          </button>
          <button className="ghost-button" onClick={toggleTheme} type="button">
            {theme === "light" ? "Dark Theme" : "Light Theme"}
          </button>
          <button className="danger-button" onClick={onLogout} type="button">
            Logout
          </button>
        </div>
      </header>

      {(message || error) && (
        <div className={`notice ${error ? "error" : "success"}`}>{error || message}</div>
      )}

      {showSearch && (
        <section className="simple-panel">
          <p className="eyebrow">Search Products</p>
          <input
            placeholder="Search by name, category, or description"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </section>
      )}

      {showExplore && (
        <section className="simple-panel">
          <p className="eyebrow">Explore</p>
          <div className="explore-grid">
            {products.slice(0, 3).map((product) => (
              <article className="explore-card" key={product.id}>
                <strong>{product.name}</strong>
                <span>{product.category}</span>
                <p>{product.description}</p>
              </article>
            ))}
          </div>
        </section>
      )}

      <section className="main-layout">
        <section className="catalog-card">
          <div className="section-head">
            <div>
              <p className="eyebrow">Products</p>
              <h2>Card Items</h2>
            </div>
          </div>
          <div className="product-grid">
            {catalogItems.map((item) => (
              <ProductCard key={item.product.id} item={item} onBuy={buyProduct} onCancel={cancelProduct} />
            ))}
          </div>
        </section>

        <aside className="sidebar">
          <section className="simple-panel">
            <p className="eyebrow">Cart</p>
            <h2>${cartTotal.toFixed(2)}</h2>
            <div className="stack compact">
              {cart.length === 0 ? (
                <p className="subtext">Your cart is empty.</p>
              ) : (
                cart.map((item) => {
                  const product = productLookup[item.productId];
                  if (!product) {
                    return null;
                  }

                  return (
                    <article className="cart-line" key={item.productId}>
                      <div>
                        <strong>{product.name}</strong>
                        <p>
                          Qty {item.quantity} · ${product.price.toFixed(2)}
                        </p>
                      </div>
                      <button className="ghost-button" onClick={() => cancelProduct(item.productId)} type="button">
                        Cancel
                      </button>
                    </article>
                  );
                })
              )}
            </div>
          </section>

          <section className="simple-panel">
            <p className="eyebrow">Checkout</p>
            <form className="stack compact" onSubmit={checkout}>
              <input
                placeholder="Customer name"
                value={checkoutForm.customerName}
                onChange={(event) => setCheckoutForm({ ...checkoutForm, customerName: event.target.value })}
              />
              <input
                placeholder="Email"
                value={checkoutForm.email}
                onChange={(event) => setCheckoutForm({ ...checkoutForm, email: event.target.value })}
              />
              <textarea
                placeholder="Address"
                rows="4"
                value={checkoutForm.address}
                onChange={(event) => setCheckoutForm({ ...checkoutForm, address: event.target.value })}
              />
              <button className="primary-button" disabled={loading} type="submit">
                {loading ? "Buying..." : "Buy All"}
              </button>
            </form>
          </section>

          <section className="simple-panel">
            <p className="eyebrow">Orders</p>
            <div className="stack compact">
              {orders.length === 0 ? (
                <p className="subtext">No orders yet.</p>
              ) : (
                orders.map((order) => (
                  <article className="order-card" key={order.id}>
                    <strong>Order #{order.id}</strong>
                    <p>{order.customerName}</p>
                    <span className="pill">{order.status}</span>
                  </article>
                ))
              )}
            </div>
          </section>
        </aside>
      </section>
    </main>
  );
}

export default HomePage;
