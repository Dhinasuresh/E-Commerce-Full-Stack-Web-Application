function ProductCard({ item, onBuy, onCancel }) {
  const { product, quantity } = item;

  return (
    <article className="product-card">
      <div className="product-image" style={{ backgroundImage: `url(${product.imageUrl})` }} />
      <div className="product-body">
        <div className="product-row">
          <div>
            <h4>{product.name}</h4>
            <span className="muted">{product.category}</span>
          </div>
          <strong>${product.price.toFixed(2)}</strong>
        </div>
        <p>{product.description}</p>
        <div className="product-row">
          <span className="pill">{product.inventory} left</span>
          {quantity > 0 && <span className="pill soft">In cart: {quantity}</span>}
        </div>
        <div className="button-row">
          <button
            className="primary-button"
            disabled={product.inventory <= 0}
            onClick={() => onBuy(product.id)}
            type="button"
          >
            {product.inventory <= 0 ? "Sold out" : "Buy"}
          </button>
          <button
            className="ghost-button"
            disabled={quantity === 0}
            onClick={() => onCancel(product.id)}
            type="button"
          >
            Cancel
          </button>
        </div>
      </div>
    </article>
  );
}

export default ProductCard;
